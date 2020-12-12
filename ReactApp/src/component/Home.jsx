import React, { Component } from 'react';
import HouseService from '../service/HouseService';
import getImageFromURL from "../lib/GetImage";
import "../css/Home.css";


class Home extends Component {

    constructor(props) {
        super(props);
        this.state = {
            city: null,
            cheapestHouses: null,
            mostAppreciatedHouses: null
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    componentDidMount() {
        HouseService.getCheapestHouses()
            .then((res) => {
                this.setState({
                    cheapestHouses: res.data
                });
            })
            .catch((err) => {
                console.log(err.response)
            });
        HouseService.getMostAppreciatedHouses()
            .then((res) => {
                this.setState({
                    mostAppreciatedHouses: res.data
                })
            })
            .catch((err) => {
                console.log(err.response)
            });
    }

    handleSubmit(event) {
        event.preventDefault();
        this.props.history.push(`/searchHouse/${this.state.city}`);
    }

    handleChange(event) {
        event.preventDefault()
        this.setState({
            [event.target.name]: event.target.value
        });
    }



    render() {

        let cheapHouses = null;
        let mostApprecieted = null;
        const searchForm = <div>
            <form onSubmit={this.handleSubmit}>

                <span className="titleText" >
                    Search for the best houses and hotels...
                </span> <br />
                <span className="subtitleText">
                    From cheap houses to luxury hotels <br />
                </span>
                <div className="searchBlock">
                    <input name="city" placeholder="Where are you going?" onChange={this.handleChange} required/>
                    <button type="submit">Search</button>
                </div>
            </form>
        </div>

        if (this.state.cheapestHouses) {
            let images = [];
            for (let i = 0; i < this.state.cheapestHouses.length; i++) {
                images[i] = getImageFromURL(this.state.cheapestHouses[i].houseId, this.state.cheapestHouses[i].mainImage);
            }
            cheapHouses = this.state.cheapestHouses.map((data, index) => {
                return (
                    <div className="houseImageContainer">
                        <a href={`/house/${this.state.cheapestHouses[index].houseId}`}>
                            <img src={images[index]} alt='' />
                        </a>
                        <div className="houseTitle">
                            {this.state.cheapestHouses[index].houseName} <br />
                            {this.state.cheapestHouses[index].country}
                        </div>
                        <div className="housePrice">
                            Starting with {this.state.cheapestHouses[index].costPerNight} lei
                            </div>
                        <div className="reviewAndNumber">
                            <div className="reviewScore">
                                {this.state.cheapestHouses[index].rating}
                            </div>
                            <div className="numberOfReviews">
                                {this.state.cheapestHouses[index].reviewNumber} reviews
                                </div>
                        </div>
                    </div>
                );
            })
        }


        if (this.state.mostAppreciatedHouses) {
            let images = [];
            for (let i = 0; i < this.state.mostAppreciatedHouses.length; i++) {
                images[i] = getImageFromURL(this.state.mostAppreciatedHouses[i].houseId, this.state.mostAppreciatedHouses[i].mainImage);
            }
            mostApprecieted = this.state.mostAppreciatedHouses.map((data, index) => {
                return (
                    <div className="houseImageContainer">
                        <a href={`/house/${this.state.mostAppreciatedHouses[index].houseId}`}>
                            <img src={images[index]} alt='' />
                        </a>
                        <div className="houseTitle">
                            {this.state.mostAppreciatedHouses[index].houseName} <br />
                            {this.state.mostAppreciatedHouses[index].country}
                        </div>
                        <div className="housePrice">
                            Starting with {this.state.mostAppreciatedHouses[index].costPerNight} lei
                            </div>
                        <div className="reviewAndNumber">
                            <div className="reviewScore">
                                {this.state.mostAppreciatedHouses[index].rating}
                            </div>
                            <div className="numberOfReviews">
                                {this.state.mostAppreciatedHouses[index].reviewNumber} reviews
                                </div>
                        </div>
                    </div>
                );
            })
        }

        return (
            <div className="homeEntireContainer">
                <div className="searchContainer">
                    {searchForm}
                </div>
                <div className="cheapHousesContainer">
                    <span className="cheapHousesTitle">
                        Clean and affodable houses
                    </span>
                    <div className="houseContainer">
                        {cheapHouses}
                    </div>
                </div>
                <div className="cheapHousesContainer">
                    <span className="cheapHousesTitle">
                        Most appreciated houses
                    </span>
                    <div className="houseContainer">
                        {mostApprecieted}
                    </div>
                </div>
            </div>



        );

    }

}

export default Home;