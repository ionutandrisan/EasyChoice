import React, { Component } from 'react';
import PersonDataService from '../service/PersonDataService';
import '../css/UserInfo.css';
import getUserImage from "../lib/GetUserImage";
import getHouseImage from "../lib/GetImage";
import HouseService from '../service/HouseService';


class UserInfo extends Component {
    constructor(props) {
        super(props);
        this.state = {
            person: [],
            message: null,
            reservation: null,
            userImage: null
        }
        this.refreshPersons = this.refreshPersons.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handlePhotoChange = this.handlePhotoChange.bind(this);
        this.sendImageToBackend = this.sendImageToBackend.bind(this);
        this.cancelReservation = this.cancelReservation.bind(this);
    }

    componentDidMount(event) {
        this.refreshPersons();
        if (localStorage.getItem("jwt")) {
            HouseService.getAllReservations(localStorage.getItem("jwt"))
                .then((response) => {
                    this.setState({
                        reservation: response.data
                    });
                })
                .catch((error) => {
                    console.log(error.data);
                })
        }
    }

    refreshPersons() {
        const email = localStorage.getItem("email")
        const jwt = localStorage.getItem("jwt")
        if (jwt !== null) {
            PersonDataService.getUserInfo(jwt, email)
                .then(
                    (response) => {
                        this.setState({ 
                            person: response.data,
                            userImage: getUserImage(response.data.personID, response.data.photoPath)
                        });
                    }
                )
                .catch((error) => {
                    if (error.response.status === 403) {
                        localStorage.clear();
                        
                    }
                })
        } else {
            
        }
    }

    handleChange(event) {
        event.preventDefault()
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    handlePhotoChange(event) {
        event.preventDefault();
        if(!event.target.files[0].type.startsWith("image")) {
            return;
        }
        this.sendImageToBackend(event.target.files[0]);
        this.refreshPersons();
    }

    sendImageToBackend(photo) {
        const jwt = localStorage.getItem("jwt");
        if (jwt !== null) {
            let formData = new FormData();
            formData.append('userImage', photo);
            console.log(this.state.userPhoto)
            PersonDataService.changeUserPhoto(jwt, this.state.person.personID, formData)
                .then((response) => {
                    if (response.status === 200) {
                        window.location.reload(false);
                    }
                })
                .catch((error) => {
                    console.log(error.response.status);
                });
        }

    }

    cancelReservation(resID) {
        HouseService.deleteReservation(resID, localStorage.getItem("jwt"))
            .then((res) => {
                if (res.status === 200) {
                    window.location.reload();
                }
            })
            .catch((error) => {
                if (error.response.status === 401) {
                    this.props.history.push(`/login`);
                } else {
                    console.log(error.response);
                }
            })
    }



    render() {
        let userBlock = null;
        let img = null;
        let userReservations = null;
        if (this.state.person) {
            userBlock = <div className="userInfoContainer">
                <div className="menuContainer">
                    <div className="imgContainer">
                        <p>
                            <img src={this.state.userImage} alt="" />
                        </p>
                    </div>
                    <div className="addUserImage">
                        <label htmlFor="userPhoto">
                            <p>Upload photo</p>
                        </label>
                        <form className="hidden">
                            <input name="userPhoto" id="userPhoto" type="file" accept="image/*" style={{ visibility: "hidden" }} onChange={this.handlePhotoChange} /> <br />
                        </form>
                    </div>
                    <div className="nameContainer">
                        <p> {this.state.person.firstName + ' ' + this.state.person.lastName} </p>
                    </div>
                    <div className="userMenuContainer">
                        <ul>
                            <li>
                                <a href="/addHouse/houseInfo">
                                    Become a host
                        </a>
                            </li>
                            <li>
                                <a href="/mystatistics">
                                    Statistics
                        </a>
                            </li>
                        </ul>
                    </div>
                </div>
                <div className="userDetailsContainer">
                    <div className="detailsText">
                        <p>
                            My account details <br />
                        </p>
                    </div>
                    <div className="labelsInfo">
                        <form>
                            <table>
                                <tbody>
                                    <tr>
                                        <td style={{ alignSelf: "right" }}>Name:</td>
                                        <td align="left">{this.state.person.firstName + ' ' + this.state.person.lastName} </td>
                                    </tr>
                                    <tr>
                                        <td style={{ alignSelf: "right" }}>Email:</td>
                                        <td align="left">{this.state.person.email}</td>
                                    </tr>
                                    <tr>
                                        <td style={{ alignSelf: "right" }}>Telephone number:</td>
                                        <td align="left">{this.state.person.phoneNumber} </td>
                                    </tr>
                                    <tr>
                                        <td style={{ alignSelf: "right" }}>Birth date</td>
                                        <td align="left">{this.state.person.birthDate}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </form>
                    </div>
                </div> <br />
            </div>

            if (this.state.reservation != null) {
                const singleReservation = this.state.reservation.map((data) => {
                    const resImg = getHouseImage(data.houseID, data.mainImage);
                    var resCIn = data.checkInDate.split("-");
                    var resCOut = data.checkOutDate.split("-");
                    const checkInDate = new Date(resCIn[0], resCIn[1], resCIn[2]);
                    const numberOfDays = Math.floor((checkInDate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
                    console.log(numberOfDays);
                    return (
                        <div className="singleReservationContainer">
                            <div className="reservationHouseImg">
                                <a href={`/house/${data.houseID}`} >
                                    <img src={resImg} alt='' />
                                </a>
                            </div>
                            <div className="reservationText">
                                You booked {data.houseName} ({data.city}) from {resCIn[2]}-{resCIn[1]}-{resCIn[0]} to {resCOut[2]}-{resCOut[1]}-{resCOut[0]}. We hope you will enjoy your time!
                            </div>
                            <div className="reservationButtonsContainer">


                                {
                                    numberOfDays > 1 ?
                                        <>
                                            <a href={`/house/${data.houseID}`}>
                                                <button>
                                                    Check house
                                                </button> <br />
                                            </a>
                                            <p>
                                                Changed your mind?
                                            </p>
                                            <button onClick={() => {
                                                this.cancelReservation(data.bookingID);
                                            }}>
                                                Cancel reservation
                                            </button>
                                        </>
                                        :
                                        <>
                                            <p>
                                                Write a review?
                                            </p>
                                            <a href={`/house/${data.houseID}`}>
                                                <button style={{ marginBottom: "20%" }}>
                                                    See house
                                                </button> <br />
                                            </a>
                                        </>
                                }
                            </div>

                        </div>
                    )
                })

                userReservations = <div className="userReservationsContainer">
                    <div className="userReservations">
                        <div className="userReservationsTitle">
                            My reservations
                    </div>
                        <div >
                            {singleReservation}
                        </div>
                    </div>
                </div>
            }
        }

        return (
            <>
                {userBlock}
                {userReservations}
            </>
        );
    }
}

export default UserInfo;