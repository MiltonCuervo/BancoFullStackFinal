// src/components/CustomerList.js

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom'; // 1. Importa Link para la navegación
import './Components.css';

const CustomerList = () => {
    const [customers, setCustomers] = useState([]);

    // Función para obtener los clientes
    const fetchCustomers = () => {
        axios.get('http://localhost:8080/api/customers')
            .then(response => {
                setCustomers(response.data);
            })
            .catch(error => {
                console.error('¡Hubo un error al obtener los clientes!', error);
            });
    };

    useEffect(() => {
        fetchCustomers();
    }, []);

    // 2. Función para manejar el borrado
    const handleDelete = (customerId) => {
        // Pedimos confirmación al usuario
        if (window.confirm('¿Estás seguro de que deseas borrar este cliente?')) {
            axios.delete(`http://localhost:8080/api/customers/${customerId}`)
                .then(response => {
                    alert('¡Cliente borrado exitosamente!');
                    // Actualizamos la lista de clientes para reflejar el cambio
                    fetchCustomers();
                })
                .catch(error => {
                    console.error('¡Hubo un error al borrar el cliente!', error);
                    alert('Error al borrar el cliente.');
                });
        }
    };

    return (
        <div className="card">
            <h2>Lista de Clientes</h2>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Número de Cuenta</th>
                    <th>Saldo</th>
                    <th>Acciones</th> {/* 3. Añade la columna de Acciones */}
                </tr>
                </thead>
                <tbody>
                {customers.map(customer => (
                    <tr key={customer.id}>
                        <td>{customer.id}</td>
                        <td>{customer.firstName}</td>
                        <td>{customer.lastName}</td>
                        <td>{customer.accountNumber}</td>
                        <td>${new Intl.NumberFormat('es-CO').format(customer.balance)}</td>
                        <td>
                            {/* 4. Botón de Editar que lleva a una nueva página */}
                            <Link to={`/clientes/editar/${customer.id}`} className="action-button edit">
                                Editar
                            </Link>
                            {/* 5. Botón de Borrar que llama a nuestra función */}
                            <button onClick={() => handleDelete(customer.id)} className="action-button delete">
                                Borrar
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default CustomerList;