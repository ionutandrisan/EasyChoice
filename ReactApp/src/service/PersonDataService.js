import axios from 'axios';
import AddJwtToHeader from '../lib/AddJwtToHeader';
import { API } from '../constants/api';

class PersonDataService {
    
    retrievePerson() {
        return axios.get(`${API}/prs`);
    }

    insertPerson(person) {
        return axios.post(`${API}/register`, person);
    }

    loginUser(person) {
        return axios.post(`${API}/login`, person);
    }

    
    getUserInfo(jwt) {
        var header = AddJwtToHeader(jwt);
        return axios.get(`${API}/userinfo`,  {
            headers: header
        } );
    }

    changeUserPhoto(jwt, userId, data) {
        var header = { 
            'Authorization': `Bearer ${jwt}`
        }
        return axios.post(`${API}/changeUserPhoto/${userId}`, data, {
            headers: header
        });
    }

    getName(jwt) {
        const header = AddJwtToHeader(jwt);
        return axios.get(`${API}/statistics/getname`, {
            headers: header
        });
    }

    getNoClientsForGraph(jwt) {
        const header = AddJwtToHeader(jwt);
        return axios.get(`${API}/statistics/getNoClientsPerMonth`, {
            headers: header
        });
    }

    getHousesName(jwt) {
        const header = AddJwtToHeader(jwt);
        return axios.get(`${API}/statistics/userHouses`, {
            headers: header
        });
    }

    getHousesReviews(jwt) {
        const header = AddJwtToHeader(jwt);
        return axios.get(`${API}/statistics/houseReviewsNumber`, {
            headers: header
        });
    }

    getReviewRatingsForUser(jwt) {
        const header = AddJwtToHeader(jwt);
        return axios.get(`${API}/statistics/houseReviewRatings`, {
            headers: header
        });
    }

    getNrOfClientsForHouse(jwt, houseID) {
        const header = AddJwtToHeader(jwt);
        return axios.get(`${API}/statistics/getNoClientsPerMonth/${houseID}`, {
            headers: header
        });
    }

    getNrReviewsForHouse(jwt, houseID) {
        const header = AddJwtToHeader(jwt);
        return axios.get(`${API}/statistics/houseReviewsNumber/${houseID}`, {
            headers: header
        });
    }

    getReviewRatingsForHouse(jwt, houseID) {
        const header = AddJwtToHeader(jwt);
        return axios.get(`${API}/statistics/houseReviewRatings/${houseID}`, {
            headers: header
        });
    }
}

export default new PersonDataService()