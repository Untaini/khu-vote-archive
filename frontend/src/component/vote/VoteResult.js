import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import "./VoteResult.css";

const apiUrl = process.env.REACT_APP_API_URL;

function VoteResult() {
    const [voteResult, setVoteResult] = useState(null);

    const token = localStorage.getItem('token');

    const navigate = useNavigate();
    const { id } = useParams();

    useEffect(() => {
        fetchVoteResult();
    }, []);

    const fetchVoteResult = async () => {
        try {
            const response = await axios.get(`${apiUrl}/vote/${id}/result`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });

            setVoteResult(response.data);
        } catch (error) {
            console.log('fetchVoteResult Failed');
            alert(error.response.data.errorMessage);
            navigate('/vote/list');
        }
    };

    const loadResultOption = (candidateTurnout) => {
        return (
            <div className="result-option">
                <div className="option-name">
                    {candidateTurnout.candidateOption}
                </div>
                <div className="option-turnout">
                    {candidateTurnout.turnoutPercent}
                </div>
            </div>
        );
    }

    return (
        <div className="vote-result-container">
            { voteResult ? (
                <div className="result-card">
                    <div className="result-title">
                        &lt; {voteResult.title} &gt; 
                    </div>
                    <div className="result-affiliation">
                        {voteResult.affiliation}
                    </div>
                    <div className="result-option-list">
                        {voteResult.voteResult.map((candidateTurnout) => 
                            loadResultOption(candidateTurnout)
                        )}
                    </div>
                    <div className="result-footer">
                        <button className="back-button" onClick={() => navigate('/vote/list')}>뒤로가기</button>
                    </div>
                </div>
            ) : (
                <div className="loading">
                    Loading...
                </div>
            )}
        </div>
    );
}

export default VoteResult;