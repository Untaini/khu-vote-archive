import { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import axios from "axios";
import "./VoteList.css";

const apiUrl = process.env.REACT_APP_API_URL;

function VoteList() {
    const token = localStorage.getItem('token');

    const [voteItems, setVoteItems] = useState(null);

    const navigate = useNavigate();

    useEffect(() => {
        const handleInit = async () => {
            if (await verifyToken()) {
                await fetchVoteItems();
            } else {
                localStorage.removeItem('token');
                navigate('/');
            }
        };
        
        const verifyToken = async () =>{
            try {
                const response = await axios.get(`${apiUrl}/auth/google/verify`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });

                return response.data.isVerified;
            } catch (error) {
                console.log('verifyToken Failed');
                return false;
            }
        };

        const fetchVoteItems = async () => {
            try {
                const response = await axios.get(`${apiUrl}/vote/list`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });

                setVoteItems(response.data);
                console.log(response.data);
            } catch (error) {
                console.log('fetchVoteItems Failed');
            }
        };

        handleInit();
    }, [navigate, token]);

    const padTwoDigits = (number) => {
        return number < 10 ? `0${number}` : number;
    }

    const dateFormat = (date) => {
        const year = date.getFullYear();
        const month = padTwoDigits(date.getMonth() + 1);
        const day = padTwoDigits(date.getDate());
        const hours = padTwoDigits(date.getHours());
        const minutes = padTwoDigits(date.getMinutes());

        return `${year}.${month}.${day} ${hours}:${minutes}`;
    }

    const loadVoteItem = (voteItem) => {
        return (
            <div className="card">
                <div className="card-container">
                    <div className="card-header">
                        {
                            voteItem.isVotingPeriod ? (
                                <div className="voting-green-icon" />
                             ) : (
                                <div className="voting-red-icon" />
                             )
                        }
                        <div className="voting-period">
                            {dateFormat(new Date(voteItem.startTime))} ~ {dateFormat(new Date(voteItem.endTime))}
                        </div>
                    </div>
                    <div className="card-body">
                        <div className="turnout-description">
                            투표율
                        </div>
                        <div className="turnout-percent">
                            {voteItem.turnoutPercent}
                        </div>
                        <div className="vote-title">
                            {voteItem.title}
                        </div>
                        <div className="affiliation-name">
                            {voteItem.affiliation}
                        </div>
                    </div>
                    <div className="card-footer">
                        {
                            new Date(voteItem.endTime) < new Date().getTime() ? (
                                <button className="vote-result-button" onClick={() => navigate(`/vote/${voteItem.voteId}/result`)}>결과 확인</button>
                            ) : voteItem.isVoted ? (
                                <button className="vote-button-disable" disabled>투표 완료</button>
                            ) : voteItem.isVotingPeriod ? (
                                <button className="vote-button" onClick={() => navigate(`/vote/${voteItem.voteId}`)}>투표하기</button>
                            ) : (
                                <button className="vote-button-disable" disabled>투표 대기</button> 
                            )
                        }
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="vote-list-container">
            <div className="list-title">
                &lt; 투표 목록 &gt; 
            </div>
            <div className="list-body">
                <div className="items-container">
                    {
                        voteItems ? (
                            voteItems.votePreviewInfos.length > 0 ? (
                            voteItems.votePreviewInfos.map((voteItem) => (
                                loadVoteItem(voteItem)
                            ))) : (
                                <div className="empty-list">투표 목록이 없습니다.</div>
                            )
                        ) : (
                            <div className="loading">Loading...</div>
                        )
                    }
                    {/*}
                    <div className="card">
                        <div className="card-container">
                            <div className="card-header">
                                <div className="voting-green-icon" />
                                <div className="voting-period">
                                    2021.09.01 09:30 ~ 2021.09.30 18:00
                                </div>
                            </div>
                            <div className="card-body">
                                <div className="turnout-description">
                                    투표율
                                </div>
                                <div className="turnout-percent">
                                    10.00%
                                </div>
                                <div className="vote-title">
                                    투표 테스트
                                </div>
                                <div className="affiliation-name">
                                    컴퓨터공학부
                                </div>
                            </div>
                            <div className="card-footer">
                                <button className="vote-button">투표하기</button>
                            </div>
                        </div>
                    </div>
                    */}
                </div>
            </div>
        </div>
    );
};

export default VoteList;