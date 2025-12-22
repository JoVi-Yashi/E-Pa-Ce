import axios from 'axios';

// Determine Base URL
let baseURL = import.meta.env.VITE_API_URL;

if (!baseURL) {
  const hostname = window.location.hostname;
  // If we are on a local network (e.g., 192.168.x.x) but not localhost, 
  // try to connect to the backend on the same IP at port 8081
  if (hostname !== 'localhost' && hostname !== '127.0.0.1') {
      // Check if we are potentially on Ngrok (https)
      if (window.location.protocol === 'https:') {
          console.warn('Running on HTTPS/Ngrok without VITE_API_URL set. Backend connection might fail if not also exposed.');
      }
      baseURL = `http://${hostname}:8081/api`; 
  } else {
      baseURL = 'http://localhost:8081/api';
  }
}

const api = axios.create({
  baseURL: baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    const activeRole = localStorage.getItem('activeRole');
    if (activeRole) {
      config.headers['X-Active-Role'] = activeRole;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
