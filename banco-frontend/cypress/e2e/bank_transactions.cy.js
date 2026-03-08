describe('Pruebas E2E del Banco - Robot de Interfaz', () => {

    before(() => {
        // Poblamos la base de datos de Spring Boot antes de todas las pruebas visuales E2E
        cy.request({ method: 'POST', url: 'http://localhost:8080/api/customers', failOnStatusCode: false, body: { firstName: "Homero", lastName: "Simpson", accountNumber: "0001", balance: 1000.00 } });
        cy.request({ method: 'POST', url: 'http://localhost:8080/api/customers', failOnStatusCode: false, body: { firstName: "Ned", lastName: "Flanders", accountNumber: "0002", balance: 500.00 } });
    });

    beforeEach(() => {
        // Antes de cada prueba el bot entrará directamente a nuestro servidor de React.
        cy.visit('/');
    });

    it('Caso E2E 1: Flujo Cero a Cien (Transferencia Exitosa)', () => {
        // Simulamos clic en la barra de navegación para ir a /transferencia
        cy.get('nav a').contains('Realizar Transferencia').click();

        // El bot debería ver la nueva URL
        cy.url().should('include', '/transferencia');

        // Llenamos el formulario escribiendo como si fuera teclado real
        cy.get('input[type="text"]').eq(0).type('0001'); // Cuenta origen (Homero)
        cy.get('input[type="text"]').eq(1).type('0002'); // Cuenta destino (Ned)
        cy.get('input[type="number"]').type('50.00');    // Monto

        // Hacemos clic en el botón transferir
        cy.get('button[type="submit"]').contains('Transferir').click();

        // ESPERAMOS A QUE APAREZCA EL ALERTA VERDE (El Request pasa por Spring Boot y vuelve a React)
        // Esto verifica todo: Frontend -> Backend -> Base de Datos -> Backend -> Frontend
        cy.contains('¡Transferencia exitosa! ID de transacción:').should('be.visible');
    });

    it('Caso E2E 2: Llenado de formulario inválido (Validación React Native)', () => {
        cy.visit('/transferencia');

        // Llenamos solo 1 campo dejando los demás vacios para romperlo
        cy.get('input[type="number"]').type('100.00');
        cy.get('button[type="submit"]').contains('Transferir').click();

        // Esperamos que el navegador rechace el envío por campos "required" (HTML5 Validation)
        cy.get('input:invalid').should('have.length', 2);
    });

    it('Caso E2E 3: Manejo de errores visuales (Validación Backend)', () => {
        cy.visit('/transferencia');

        cy.get('input[type="text"]').eq(0).type('1111'); // Cuentas que no existen
        cy.get('input[type="text"]').eq(1).type('2222');
        cy.get('input[type="number"]').type('10.00');

        cy.get('button[type="submit"]').contains('Transferir').click();

        // El backend debe decir que la cuenta no existe, y el Frontend atrapar el error
        // y pasarlo de Axios a la alerta roja.
        cy.contains('Error en la transferencia').should('be.visible');
    });
});
