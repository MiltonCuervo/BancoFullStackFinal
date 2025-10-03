// src/components/TransactionForm.js

import React, { useState } from 'react';
import axios from 'axios';
import './Components.css'; // Importa los estilos compartidos

const TransactionForm = () => {
    const [senderAccountNumber, setSenderAccountNumber] = useState('');
    const [receiverAccountNumber, setReceiverAccountNumber] = useState('');
    const [amount, setAmount] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = (event) => {
        event.preventDefault();
        setMessage('');

        const transactionData = {
            senderAccountNumber: senderAccountNumber,
            receiverAccountNumber: receiverAccountNumber,
            amount: parseFloat(amount)
        };

        axios.post('http://localhost:8080/api/transactions', transactionData)
            .then(response => {
                setMessage(`¡Transferencia exitosa! ID de transacción: ${response.data.id}`);
                setSenderAccountNumber('');
                setReceiverAccountNumber('');
                setAmount('');
            })
            .catch(error => {
                const errorMessage = error.response ? error.response.data : "Error desconocido, intente de nuevo.";
                setMessage(`Error en la transferencia: ${errorMessage}`);
            });
    };

    return (
        <div className="card">
            <h2>Realizar Transferencia</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Cuenta de Origen:</label>
                    <input
                        type="text"
                        value={senderAccountNumber}
                        onChange={e => setSenderAccountNumber(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Cuenta de Destino:</label>
                    <input
                        type="text"
                        value={receiverAccountNumber}
                        onChange={e => setReceiverAccountNumber(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Monto:</label>
                    <input
                        type="number"
                        step="0.01"
                        value={amount}
                        onChange={e => setAmount(e.target.value)}
                        placeholder="Ej: 150.00"
                        required
                    />
                </div>
                <button type="submit">Transferir</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
};

export default TransactionForm;