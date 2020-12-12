import React, { Component } from 'react';
import HouseService from '../service/HouseService';
import getImageFromURL from "../lib/GetImage";
import "../css/SearchHouses.css";
import getCurrentDate from '../lib/GetCurrentDate';

class SearchHouses extends Component {

    constructor(props) {
        super(props);
        this.state = {
            city: this.props.match.params.city,
            houses: null, 
            
        }

        this.getHouses = this.getHouses.bind(this);
        this.searchHouses = this.searchHouses.bind(this);
        this.handleCityAndDatesChange = this.handleCityAndDatesChange.bind(this);
    }

    handleCityAndDatesChange(event) {
        this.setState({
            [event.target.name]: event.target.value, 
        });
    }

    componentDidMount(event) {
        this.getHouses();
    }


    getHouses() {
        HouseService.searchHouseAfterCity(this.state.city)
            .then((response) => {
                if (response.status === 200) {
                    this.setState({
                        houses: response.data
                    })
                    console.log(this.state.houses);
                }
            })
            .catch((error) => {
                if (error.response.status === 404) {
                    console.log("Eroare incarcare casa");
                } else if (error.response.status === 409) {
                    console.log("Conflict")
                }
            })
    }

    searchHouses() {

    }

    render() {

        let searchText = null;
        let houseContainer = null;


        if (this.state.houses) {
            searchText = <div className="searchTitleContainer">
                {this.state.city}: there were found {this.state.houses.length} places to stay
            </div>

            houseContainer = this.state.houses.map((data) => {
                const img = getImageFromURL(data.houseId, data.mainImage);
                return (
                    <div className="searchHouseContainer">
                        <img src={img} alt='' />
                        <div className="leftsHouseContainer">
                            <a href={`/house/${data.houseId}`}>
                                <div className="searchHouseNameContainer">
                                    {data.houseName}
                                </div>
                            </a>
                            <div className="searchHousesLocationSubtitle">
                                {data.city}, {data.country}
                            </div>
                            <div className="serachHousesDescription">
                                {data.description}
                            </div>
                        </div>
                        <div className="rightsHouseContainer">
                            <div className="searchHouseReviewContainer">
                                <div className="searchHouseRNumber">
                                    {data.reviewNumber} reviews
                            </div>
                                <div className="searchHouseRScore">
                                    {data.rating}
                                </div>
                            </div>
                            <div className="searchHouseNrContainer">
                                1 night
                            </div>
                            <div className="seachHousePriceContainer">
                                {data.costPerNight} lei
                            </div>
                            <div className="searchHouseTaxesContainer">
                                no additional taxes
                            </div>
                            <div className="searchHouseDetailsButtonContainer">
                                <a href={`/house/${data.houseId}`}>
                                    <button>
                                        View details
                                    </button>
                                </a>
                            </div>
                        </div>

                    </div>
                );
            })

        }


        return (
            <>
                <div className="entireContainer">
                    {searchText}
                    <div className="searchAllHousesContainer">
                        {houseContainer}
                    </div>
                </div>
            </>
        );

    }
}

export default SearchHouses;