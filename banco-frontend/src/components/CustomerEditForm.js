// src/components/CustomerEditForm.js

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom'; // Hooks para obtener el ID de la URL y para navegar
import './Components.css';

const CustomerEditForm = () => {
    // useParams nos da los parámetros de la URL, en este caso el :id
    const { id } = useParams();
    // useNavigate nos permite redirigir al usuario a otra página
    const navigate = useNavigate();

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [message, setMessage] = useState('');

    // Este useEffect se ejecuta cuando el componente se carga para obtener los datos del cliente
    useEffect(() => {
        axios.get(`http://localhost:8080/api/customers/${id}`)
            .then(response => {
                setFirstName(response.data.firstName);
                setLastName(response.data.lastName);
            })
            .catch(error => {
                console.error('Error al obtener los datos del cliente:', error);
                setMessage('No se pudo cargar la información del cliente.');
            });
    }, [id]); // Se ejecuta cada vez que el ID de la URL cambia

    const handleSubmit = (event) => {
        event.preventDefault();
        const updatedCustomer = { firstName, lastName };

        axios.put(`http://localhost:8080/api/customers/${id}`, updatedCustomer)
            .then(response => {
                alert('¡Cliente actualizado exitosamente!');
                // Redirigimos al usuario de vuelta a la lista de clientes
                navigate('/clientes');
            })
            .catch(error => {
                setMessage('Error al actualizar el cliente.');
                console.error('Error al actualizar:', error);
            });
    };

    return (
        <div className="card">
            <h2>Editar Cliente (ID: {id})</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Nombre:</label>
                    <input
                        type="text"
                        value={firstName}
                        onChange={e => setFirstName(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Apellido:</label>
                    <input
                        type="text"
                        value={lastName}
                        onChange={e => setLastName(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Guardar Cambios</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
};

export default CustomerEditForm;