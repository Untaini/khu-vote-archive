import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import "./VoteCreationForm.css";

const apiUrl = process.env.REACT_APP_API_URL;

const VoteCreationForm = () => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [totalVoters, setTotalVoters] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [choices, setChoices] = useState(['']);
  const [adminPassword, setAdminPassword] = useState('');
  const [passwordConfirmed, setPasswordConfirmed] = useState(false);
  const [affiliation, setAffiliation] = useState(null);

  const navigate = useNavigate();

  const addChoiceField = () => {
    setChoices([...choices, '']);
  };

  const removeChoiceField = (index) => {
    const newChoices = choices.filter((_, i) => i !== index);
    setChoices(newChoices);
  };

  const updateChoice = (index, value) => {
    const newChoices = [...choices];
    newChoices[index] = value;
    setChoices(newChoices);
  };

  const handlePasswordVerification = async () => {
    try {
      const response = await axios.post(`${apiUrl}/affiliation/verify`, {
        password: adminPassword
      });

      setPasswordConfirmed(true);
      setAffiliation(response.data.affiliation);
      alert('소속을 불러왔습니다.');

    } catch (error) {
      setPasswordConfirmed(false);
      setAffiliation(null);
      alert('잘못된 비밀번호입니다.');
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!passwordConfirmed) {
      alert('소속을 확인해주세요.');
      return;
    }

    if (totalVoters < 1) {
      alert('투표자 수는 최소 1명 이상이어야 합니다.');
      return;
    }

    const formatDateTime = (dateTimeString) => {
      const date = new Date(dateTimeString);
      return date.toISOString().replace('Z', '+09:00');
    };

    const voteData = {
      title,
      description,
      voterCount: totalVoters,
      startTime: formatDateTime(startTime),
      endTime: formatDateTime(endTime),
      candidates: choices.filter(choice => choice.trim() !== ''),
      affiliationPassword: adminPassword
    };

    try {
      axios.post(`${apiUrl}/vote/create`, voteData);
      alert('투표가 성공적으로 생성되었습니다.');
      navigate('/vote/create');
    } catch (error) {
      alert('투표 생성에 실패했습니다.');
    }
  };

  return (
    <div className="background">
      <div className="form-container">
        <h1 className="title">새 투표 생성</h1>
        
        <form onSubmit={handleSubmit} className="form">
          <div className="form-group">
            <label>투표 제목</label>
            <input 
              className="input"
              value={title} 
              onChange={(e) => setTitle(e.target.value)} 
              placeholder="투표 제목을 입력하세요" 
              required
            />
          </div>

          <div className="form-group">
            <label>투표 상세 설명</label>
            <textarea 
              className="description input"
              value={description} 
              onChange={(e) => setDescription(e.target.value)} 
              placeholder="투표에 대한 상세 설명을 입력하세요" 
              rows={4}
              required
            />
          </div>

          <div className="form-group">
            <label>투표 소속</label>

            {!passwordConfirmed ? (
              <div className="password-container">
                <input 
                  type="password" 
                  value={adminPassword} 
                  onChange={(e) => setAdminPassword(e.target.value)} 
                  placeholder="소속 비밀번호를 입력하세요" 
                  required 
                  disabled={passwordConfirmed}
                  className="password-input input"
                />
                <button 
                  type="button" 
                  onClick={handlePasswordVerification}
                >
                  확인
                </button>
              </div>
              ) : (
                <input 
                  type="text" 
                  value={affiliation || ''} 
                  readOnly 
                  className="affiliation-input input"
                />
              )}
          </div>

          <div className="form-group">
            <label>총 투표자 수</label>
            <input 
              type="number" 
              value={totalVoters} 
              onChange={(e) => setTotalVoters(e.target.value)} 
              placeholder="총 투표자 수를 입력하세요" 
              required 
              className="input"
            />
          </div>

          <div className="vote-time-group">
            <div className="form-group">
              <label>투표 시작 시각</label>
              <input 
                type="datetime-local" 
                value={startTime} 
                onChange={(e) => setStartTime(e.target.value)} 
                required 
                className="input"
              />
            </div>
            <div className="form-group">
              <label>투표 종료 시각</label>
              <input 
                type="datetime-local" 
                value={endTime} 
                onChange={(e) => setEndTime(e.target.value)} 
                required 
                className="input"
              />
            </div>
          </div>

          <div className="form-group">
            <label>투표 선택지</label>
            {choices.map((choice, index) => (
              <div key={index} className="choice-container">
                <input 
                  value={choice} 
                  onChange={(e) => updateChoice(index, e.target.value)} 
                  placeholder={`선택지 ${index + 1}`}
                  className="choice-input input"
                  required={index === 0}
                />
                {index > 0 && (
                  <button 
                    type="button" 
                    variant="destructive" 
                    size="icon" 
                    onClick={() => removeChoiceField(index)}
                    className="button danger"
                  >
                    삭제
                  </button>
                )}
              </div>
            ))}
            <button 
              type="button" 
              variant="outline" 
              onClick={addChoiceField}
              className="button outline"
            >
              + 선택지 추가
            </button>
          </div>

          <button 
            type="submit" 
            disabled={!passwordConfirmed}
            className="button submit"
          >
            투표 생성
          </button>
        </form>
      </div>
    </div>
  );
};

export default VoteCreationForm;
