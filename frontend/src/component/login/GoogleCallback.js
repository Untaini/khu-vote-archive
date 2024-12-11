import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const apiUrl = process.env.REACT_APP_API_URL;

const GoogleCallback = (props) => {
    const params = new URL(document.URL).searchParams;
    const code = params.get('code');
    const navigate = useNavigate();

    useEffect(() => {

    const fetchData = async () => {
        try {
            const response = await axios.get(`${apiUrl}/auth/google/token?code=${code}`);
            
            localStorage.setItem('token', response.data.token);
        } catch (error) {
            console.log('googleLogin Failed');
        
        }
        
        navigate('/');         
    };
    
    console.log('GoogleCallback');
    fetchData();
    }, [navigate, code]);
};

export default GoogleCallback;