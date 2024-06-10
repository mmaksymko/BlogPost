import React, { useState } from 'react';
import './App.css';

import Home from "./pages/Home";

import Header from './components/Header';
import Footer from './components/Footer';

import { BrowserRouter, Routes, Route } from "react-router-dom";
import { SnackBarContext, defaultSnackBar, SnackBarState } from './contexts/SnackBarContext';
import { Alert, Snackbar } from '@mui/material';
import axios from 'axios';
import { serverURL } from './config';
import NoPage from './pages/NoPage';
import { AuthContext, unauthorizedUser, UserState } from './contexts/AuthContext';
import OAauthRedirectHandler from './pages/OAauthRedirectHandler';

function App() {
  const [user, setUser] = useState<UserState>(unauthorizedUser);
  user.fetchUser = () => {
    axios.get(`${serverURL}/users/`, { withCredentials: true }).then(response => setUser({ ...response.data }));
  }
  const [snack, setSnackBar] = useState<SnackBarState>(defaultSnackBar);
  const handleClose = (event?: React.SyntheticEvent | Event, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackBar(prev => ({ ...prev, open: false }));
  };

  return (
    <AuthContext.Provider value={{ ...user, setUser }}>
      <SnackBarContext.Provider value={{ ...snack, setSnackBar }}>
        <BrowserRouter>
          <Header />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="oauth2/redirect" element={<OAauthRedirectHandler />} />
            <Route path="*" element={<NoPage />} />
          </Routes>
          <Footer />
        </BrowserRouter >
        <Snackbar anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }} open={snack.open} autoHideDuration={6000} onClose={handleClose}>
          <Alert onClose={handleClose} severity={snack.severity} variant="standard" sx={{ width: '125%', maxWidth: "75vw" }}>
            {snack.message}
          </Alert>
        </Snackbar>
      </SnackBarContext.Provider>
    </AuthContext.Provider >
  );
}

export default App;
