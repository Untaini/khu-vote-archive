import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Login.css';

const apiUrl = process.env.REACT_APP_API_URL;

function Login() {
  const navigate = useNavigate();

  useEffect(() => {
    console.log('Login');
    const token = localStorage.getItem('token');

    if (token) {
        navigate('/vote/list');
    }
  }, [navigate]);


  const handleGoogleLogin = () => {
    const callGoogleLogin = async () => {
        try {
            const response = await axios.get(`${apiUrl}/auth/google`);
            window.location.href = response.data;
        } catch (error) {
            console.log('googleLogin Failed');
        }            
    };

    callGoogleLogin();
  };

  return (
      <div className="login-container">
        <div className='icon' />
        <h1>&lt; KHU Vote &gt;</h1>
        <button className='google-login-button' onClick={handleGoogleLogin} />
      </div>
  );
}

export default Login;
