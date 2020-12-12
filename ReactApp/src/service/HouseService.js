import axios from 'axios';
import AddJwtToHeader from '../lib/AddJwtToHeader';
import { API } from "../constants/api";


class HouseService {

    getLocationID(jwt, location) {
        var header = AddJwtToHeader(jwt);
        return axios.post(`${API}/addHouse/location`, location, {
            headers: header
        });
    }

    setHouseInfo(jwt, houseInfo) {
        var header = { 
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${jwt}`
        }
        return axios.post(`${API}/addHouse/houseInfo`, houseInfo, {
            headers: header
        });
    }

    getUserHousesInfo(jwt) {
        var header = AddJwtToHeader(jwt);
        return axios.get(`${API}/myhouses`, {
            headers: header
        })
    }

    getImageURL(imgURL) {
        return axios.get(imgURL);
    }

    verifyAvailabilityDate(data, houseID) {
        return axios.post(`${API}/house/verifyAvailability/${houseID}`, data);
    }

    getHouseInfo(houseID) {
        return axios.get(`${API}/house/${houseID}`);
    }

    getFacilities() {
        return axios.get(`${API}/house/getAllFacilities`);
    }

    getHouseReviews(houseID) {
        return axios.get(`${API}/house/getAllHouseReviews/${houseID}`);
    }

    getHouseFacilities(houseID) {
        return axios.get(`${API}/house/getHouseFacilities/${houseID}`);
    }

    addNewReview(data, houseID, jwt) {
        const header = AddJwtToHeader(jwt);
        return axios.post(`${API}/house/addNewReview/${houseID}`, data, {
            headers: header
        });
    }

    bookHouse(data, houseID, jwt) {
        const header = AddJwtToHeader(jwt);
        return axios.put(`${API}/house/bookHouse/${houseID}`, data, {
            headers: header
        })
    }

    searchHouseAfterCity(city) {
        return axios.get(`${API}/searchHouse/${city}`);
    }

    getCheapestHouses() {
        return axios.get(`${API}/home/cheapestHouses`);
    }

    getMostAppreciatedHouses() {
        return axios.get(`${API}/home/mostAppreciatedHouses`);
    }


    getAllReservations(jwt) {
        const header = AddJwtToHeader(jwt);
        return axios.get(`${API}/myreservations`, {
            headers: header
        });
    }
    
    deleteReservation(resID, jwt) {
        const header = AddJwtToHeader(jwt);
        return axios.delete(`${API}/deleteReservation/${resID}`, {
            headers: header
        })
    }

}

export default new HouseService();