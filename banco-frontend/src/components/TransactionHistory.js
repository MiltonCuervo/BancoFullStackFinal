// src/components/TransactionHistory.js

import React, { useState } from 'react';
import axios from 'axios';
import './Components.css'; // Importa los estilos compartidos

const TransactionHistory = () => {
    const [accountNumber, setAccountNumber] = useState('');
    const [transactions, setTransactions] = useState([]);
    const [message, setMessage] = useState('');

    const handleSearch = (event) => {
        event.preventDefault();
        setMessage('');
        setTransactions([]);

        axios.get(`http://localhost:8080/api/transactions/${accountNumber}`)
            .then(response => {
                if (response.data.length > 0) {
                    setTransactions(response.data);
                } else {
                    setMessage('No se encontraron transacciones para esta cuenta.');
                }
            })
            .catch(error => {
                console.error('Error al buscar transacciones:', error);
                setMessage('Error al buscar el historial. Verifique el número de cuenta.');
            });
    };

    return (
        <div className="card">
            <h2>Historial de Transacciones</h2>
            <form onSubmit={handleSearch}>
                <label>Número de Cuenta:</label>
                <input
                    type="text"
                    value={accountNumber}
                    onChange={e => setAccountNumber(e.target.value)}
                    placeholder="Ingrese número de cuenta a buscar"
                    required
                />
                <button type="submit">Buscar</button>
            </form>

            {message && <p>{message}</p>}

            {transactions.length > 0 && (
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Cuenta Origen</th>
                        <th>Cuenta Destino</th>
                        <th>Monto</th>
                        <th>Fecha</th>
                    </tr>
                    </thead>
                    <tbody>
                    {transactions.map(tx => (
                        <tr key={tx.id}>
                            <td>{tx.id}</td>
                            <td>{tx.senderAccountNumber}</td>
                            <td>{tx.receiverAccountNumber}</td>
                            <td>${new Intl.NumberFormat('es-CO').format(tx.amount)}</td>
                            <td>{new Date(tx.date).toLocaleString('es-CO')}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default TransactionHistory;