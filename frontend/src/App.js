import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './component/login/Login';
import GoogleCallback from './component/login/GoogleCallback';
import VoteList from './component/vote/VoteList';
import Vote from './component/vote/Vote';
import VoteResult from './component/vote/VoteResult';
import VoteCreationForm from './component/vote/VoteCreationForm';
import './App.css';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/google/callback" element={<GoogleCallback />} />
        <Route path="/vote/list" element={<VoteList />} />
        <Route path="/vote/:id/" element={<Vote />} />
        <Route path="/vote/:id/result" element={<VoteResult />} />
        <Route path="/vote/create" element={<VoteCreationForm />} />
        <Route path="*" element={<div>Not Found</div>} />
      </Routes>
    </Router>
  );
}

export default App;
