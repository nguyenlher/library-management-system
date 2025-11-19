import React from 'react';
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1>Library Management System - Admin Portal</h1>
        <p>Welcome to the admin interface for library management.</p>
        <p>Backend APIs are available at:</p>
        <ul>
          <li>Auth Service: http://localhost:8083</li>
          <li>User Service: http://localhost:8081</li>
          <li>Book Service: http://localhost:8082</li>
          <li>Borrow Service: http://localhost:8086</li>
          <li>Payment Service: http://localhost:8084</li>
          <li>Notification Service: http://localhost:8085</li>
        </ul>
      </header>
    </div>
  );
}

export default App;