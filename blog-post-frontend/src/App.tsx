import React, { useState } from 'react';
import './App.css';

import Home from "./pages/Home";

import Header from './components/Header';
import Footer from './components/Footer';

import { BrowserRouter, Routes, Route } from "react-router-dom";
import { SnackBarContext, defaultSnackBar, SnackBarState } from './contexts/SnackBarContext';
import { Alert, Snackbar } from '@mui/material';

function App() {
  const [snack, setSnackBar] = useState<SnackBarState>(defaultSnackBar);
  const handleClose = (event?: React.SyntheticEvent | Event, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackBar(prev => ({ ...prev, open: false }));
  };
  return (
    <SnackBarContext.Provider value={{ ...snack, setSnackBar }}>
      <BrowserRouter>
        <Header />
        <Routes>
          <Route path="/" element={<Home />} />
        </Routes>
        <Footer />
      </BrowserRouter >
      <Snackbar anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }} open={snack.open} autoHideDuration={6000} onClose={handleClose}>
        <Alert onClose={handleClose} severity={snack.severity} variant="outlined" sx={{ background: "var(--primary-color)", width: '125%', maxWidth: "75vw" }}>
          {snack.message}
        </Alert>
      </Snackbar>
    </SnackBarContext.Provider>
  );
}

export default App;
