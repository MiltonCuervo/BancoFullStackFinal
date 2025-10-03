// src/App.js

import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { FaUsers, FaExchangeAlt, FaHistory } from 'react-icons/fa';
import './App.css';

// Importamos todos los componentes que usaremos como vistas
import HomePage from './components/HomePage';
import CustomerList from './components/CustomerList';
import TransactionForm from './components/TransactionForm';
import TransactionHistory from './components/TransactionHistory';
import CustomerEditForm from './components/CustomerEditForm'; // <-- Importa el nuevo formulario de edición

function App() {
    // Estado para controlar si el menú responsive está abierto o cerrado
    const [isNavOpen, setIsNavOpen] = useState(false);

    return (
        // <Router> envuelve toda la aplicación para habilitar la navegación
        <Router>
            <div className="App">
                <header className="App-header">
                    <h1>Aplicación Bancaria</h1>

                    {/* Botón "hamburguesa" que solo es visible en pantallas pequeñas (controlado por CSS) */}
                    <button className="nav-toggle" onClick={() => setIsNavOpen(!isNavOpen)}>
                        ☰
                    </button>

                    {/* Menú de navegación. La clase cambia para mostrarse u ocultarse en móviles */}
                    <nav className={isNavOpen ? 'nav-open' : ''}>
                        <ul>
                            {/* Al hacer clic en un enlace, cerramos el menú (útil en vista móvil) */}
                            <li onClick={() => setIsNavOpen(false)}>
                                <Link to="/clientes"><FaUsers /> Ver Clientes</Link>
                            </li>
                            <li onClick={() => setIsNavOpen(false)}>
                                <Link to="/transferencia"><FaExchangeAlt /> Realizar Transferencia</Link>
                            </li>
                            <li onClick={() => setIsNavOpen(false)}>
                                <Link to="/historial"><FaHistory /> Ver Historial</Link>
                            </li>
                        </ul>
                    </nav>
                </header>

                <main>
                    {/* <Routes> define el área donde se renderizarán los componentes según la ruta */}
                    <Routes>
                        {/* Ruta para la página de inicio */}
                        <Route path="/" element={<HomePage />} />

                        {/* Ruta para la lista de clientes */}
                        <Route path="/clientes" element={<CustomerList />} />

                        {/* Nueva ruta dinámica para editar un cliente específico por su ID */}
                        <Route path="/clientes/editar/:id" element={<CustomerEditForm />} />

                        {/* Ruta para el formulario de transferencia */}
                        <Route path="/transferencia" element={<TransactionForm />} />

                        {/* Ruta para el historial de transacciones */}
                        <Route path="/historial" element={<TransactionHistory />} />
                    </Routes>
                </main>
            </div>
        </Router>
    );
}

export default App;