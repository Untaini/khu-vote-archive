// SPDX-License-Identifier: MIT
pragma solidity ^0.8.4;

import "@klaytn/contracts@1.0.6/KIP/token/KIP7/KIP7.sol";
import "@klaytn/contracts@1.0.6/security/Pausable.sol";
import "@klaytn/contracts@1.0.6/access/AccessControl.sol";

contract VotingToken is KIP7, Pausable, AccessControl {
    bytes32 private constant ADMIN_ROLE = keccak256("ADMIN_ROLE");
    bytes32 private constant VOTER_ROLE = keccak256("VOTER_ROLE");
    bytes32 private constant PAUSER_ROLE = keccak256("PAUSER_ROLE");
    bytes32 private constant CANDIDATE_ROLE = keccak256("CANDIDATE_ROLE");

    uint256 private constant TOKENS_PER_VOTER = 1;

    struct CandidateTurnout {
        string option;
        string rate;
    }

    struct Candidate {
        address addr;
        string option;
    }

    struct VoteDetail {
        string title;
        string description;
        string[] candidateOptions;
        bool isVotingPeriod;
    }

    bool public isVotingPeriod;
    mapping(uint256 => bool) private isVoted;
    mapping(string => address) private candidateMapper;
    string private voteTitle;
    string private voteDescription;
    Candidate[] private candidates;

    constructor(string memory title, string memory symbol, uint256 totalVoters, string memory description) 
        KIP7(title, symbol)
    {
        _grantRole(ADMIN_ROLE, msg.sender);
        _grantRole(ADMIN_ROLE, address(this));
        
        // Premint tokens for all voters
        _mint(msg.sender, totalVoters * TOKENS_PER_VOTER);
        isVotingPeriod = false;
        voteTitle = title;
        voteDescription = description;
    }

    event TokenDistributed(address);
    function distributeTokens(address[] memory voters) public onlyRole(ADMIN_ROLE) {
        require(voters.length <= 50, "Batch size exceeds limit");

        for (uint i = 0; i < voters.length; i++) {
            if (!hasRole(VOTER_ROLE, voters[i]) && !hasRole(CANDIDATE_ROLE, voters[i]) && balanceOf(msg.sender) > 0) {
                _setupRole(VOTER_ROLE, voters[i]);
                _transfer(msg.sender, voters[i], TOKENS_PER_VOTER);
                emit TokenDistributed(voters[i]);
            }
        }
    }

    function addCandidate(address candidateAddr, string memory description) public onlyRole(ADMIN_ROLE) {
        require(!hasRole(CANDIDATE_ROLE, candidateAddr), "Already a candidate");
        require(!hasRole(VOTER_ROLE, candidateAddr), "Already a voter");
        _setupRole(CANDIDATE_ROLE, candidateAddr);

        Candidate memory candidate = Candidate(candidateAddr, description);
        candidates.push(candidate);
        candidateMapper[description] = candidateAddr;
    }

    function vote(string memory candidateDescription, uint256 voterId) public {
        address candidateAddr = candidateMapper[candidateDescription];

        require(isVotingPeriod, "Voting is not open");
        require(hasRole(VOTER_ROLE, msg.sender), "Not a voter");
        require(hasRole(CANDIDATE_ROLE, candidateAddr), "Not a candidate");
        require(!isVoted[voterId], "Already voted");

        _transfer(msg.sender, candidateAddr, TOKENS_PER_VOTER);
        isVoted[voterId] = true;
    }

    function startVoting() public onlyRole(ADMIN_ROLE) {
        isVotingPeriod = true;
    }

    function endVoting() public onlyRole(ADMIN_ROLE) {
        isVotingPeriod = false;
        _pause();

        _revokeRole(ADMIN_ROLE, address(this));
        _revokeRole(ADMIN_ROLE, msg.sender);
        
        _grantRole(ADMIN_ROLE, address(0));
    }

    function getOverallVoterTurnout() public view returns (string memory) {
        uint256 totalVotes = 0;
        for (uint256 i = 0; i < candidates.length; i++) {
            totalVotes += balanceOf(candidates[i].addr);
        }

        uint256 turnout = (totalVotes * 10000) / totalSupply();

        return turnoutToString(turnout);
    }

    function getAllCandidatesTurnout() public view returns (CandidateTurnout[] memory) {
        require(!isVotingPeriod, "Voting is in Progress");
        require(paused(), "Voting is not ended");

        uint256 votesFromAllCandidate = 0;
        for (uint256 i = 0; i < candidates.length; i++) {
            votesFromAllCandidate += balanceOf(candidates[i].addr);
        }

        if (votesFromAllCandidate == 0) {
            votesFromAllCandidate = 1;
        }

        CandidateTurnout[] memory candidateTurnouts = new CandidateTurnout[](candidates.length);
        for (uint256 i = 0; i < candidates.length; i++) {
            uint256 turnout = (balanceOf(candidates[i].addr) * 10000) / votesFromAllCandidate;
            candidateTurnouts[i] = CandidateTurnout(candidates[i].option, turnoutToString(turnout));
        }

        return (candidateTurnouts);
    }

    function isVoteUser(uint256 voterId) public view returns (bool) {
        return isVoted[voterId];
    }

    function getVoteDetail() public view returns (VoteDetail memory) {
        string[] memory candidateOptions = new string[](candidates.length);

        for (uint i = 0; i < candidates.length; i++) {
            candidateOptions[i] = candidates[i].option;
        }

        return VoteDetail(voteTitle, voteDescription, candidateOptions, isVotingPeriod);
    }

    function turnoutToString(uint256 turnout) private pure returns (string memory) {
        uint256 integerPart = turnout / 100;
        uint256 decimalPart = turnout % 100;

        return string(abi.encodePacked(
            uintToString(integerPart), ".", 
            decimalPart < 10 ? "0" : "", // 소수점 뒤가 1자리일 경우 0 추가
            uintToString(decimalPart), "%"
        ));
    }

    function uintToString(uint256 target) private pure returns (string memory stringValue) {
        if (target == 0) {
            return "0";
        }

        uint256 targetLength = 0;
        uint256 copyOfTarget = target;
        while (copyOfTarget != 0) {
            targetLength++;
            copyOfTarget /= 10;
        }

        bytes memory byteString = new bytes(targetLength);
        while (target != 0) {
            uint8 digit = uint8(target % 10);
            targetLength--;

            byteString[targetLength] = bytes1(digit + 48);
            target /= 10;
        }

        return string(byteString);
    }

    function transfer(address recipient, uint256 amount) public virtual override returns (bool) {
        revert("CANNOT TRANSFER");
    }

    function transferFrom(address sender, address recipient, uint256 amount) public virtual override returns (bool) {
        revert("CANNOT TRANSFER");
    }

    function supportsInterface(bytes4 interfaceId) public view virtual override(KIP7, AccessControl) returns (bool) {
        return super.supportsInterface(interfaceId);
    }

    function decimals() public view virtual override returns (uint8) {
        return 0;
    }

}