import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import './Vote.css';

const apiUrl = process.env.REACT_APP_API_URL;

function Vote() {
    const token = localStorage.getItem('token');
    const { id } = useParams();

    const [voteDetail, setVoteDetail] = useState(null);
    const [selectedOption, setSelectedOption] = useState(null);
    const [isSubmitted, setIsSubmitted] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        fetchVoteDetail();
    }, []);

    const fetchVoteDetail = async () => {
        try {
            const response = await axios.get(`${apiUrl}/vote/${id}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            if (!response.data.isVotingPeriod) {
                alert('투표 기간이 아닙니다.');
                navigate('/vote/list');
            }

            setVoteDetail(response.data);

        } catch (error) {
            console.log('fetchVoteDetail Failed');
            navigate('/vote/list');
        }
    };

    const handleSubmit = async () => {
        if (selectedOption === null) {
            alert('투표할 후보를 선택해주세요.');
            return;
        }


        try {
            if (!window.confirm(`"${selectedOption}"에 투표하시겠습니까?`)) {
                return;
            }

            await axios.post(`${apiUrl}/vote/${id}`, {
                candidateOption: selectedOption
            }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            setIsSubmitted(true);

            navigate('/vote/list');
        } catch (error) {
            console.log('handleSubmit Failed');
            alert(error.response.data.errorMessage);
        }
    }

    const loadSelectedOption = (candidateOption) => {
        return (
            <div className="vote-option-container">
                <div className={`vote-option ${candidateOption === selectedOption ? "selected" : "unselected"}`}>
                    <div className="option-name" onClick={() => setSelectedOption(candidateOption)}>
                        {candidateOption}
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="background">
            <div className="vote-container">
                <div className="back-container">
                    <div className="back-button" onClick={() => navigate('/vote/list')}>
                        <div className="back-icon" />
                        <div className="back-text">뒤로가기</div>
                    </div>
                </div>

                { voteDetail ? (
                    <div className="vote-card">
                        <div className="vote-header">
                            <div className="vote-title">
                                &lt; {voteDetail.title} &gt;
                            </div>
                            <div className="vote-affiliation">
                                {voteDetail.affiliation}
                            </div>
                            <div className="vote-description">
                                {voteDetail.description}
                            </div>
                        </div>
                        <div className='vote-body'>
                            <div className="vote-select-description">
                                &lt; 하나를 선택해주세요 &gt;
                            </div>
                            <div className="vote-option-list">
                                {voteDetail.candidates.map((candidateOption) => loadSelectedOption(candidateOption))}
                            </div>
                        </div>
                    </div>
                ) : (
                    <div className="loading">
                        Loading...
                    </div>
                )}

                <div className="vote-footer">
                    <button className="vote-submit-button" onClick={handleSubmit} disabled={isSubmitted}>
                        {isSubmitted ? '투표 완료' : '투표하기'}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Vote;