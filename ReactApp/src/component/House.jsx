import React, { Component } from 'react';
import HouseService from '../service/HouseService';
import "../css/House.css";
import locationLogo from "../images/location.jpg";
import getHouseImage from "../lib/GetImage";
import getUserImage from "../lib/GetUserImage";
import StarRatingComponent from 'react-star-rating-component';
import rsl from '../rslider';
import Modal from 'react-awesome-modal';
import getCurrentDate from '../lib/GetCurrentDate';


class House extends Component {

    constructor(props) {
        super(props);
        this.state = {
            id: this.props.match.params.id,
            exists: true,
            houseInfo: null,
            checkInDate: getCurrentDate(0),
            checkOutDate: getCurrentDate(1),
            isAvailable: null,
            requestSent: null,
            validHouseMessage: null,
            noOfDays: null,
            facilities: null,
            reviews: null,
            reviewComment: null,
            reviewRating: 10,
            bookingStatus: null,
            bookingMessageVisible: false,
        }


        this.componentDidMount = this.componentDidMount.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.verifyIfAvailable = this.verifyIfAvailable.bind(this);
        this.writeReview = this.writeReview.bind(this);
        this.bookHouse = this.bookHouse.bind(this);
        this.onStarClick = this.onStarClick.bind(this);
        this.openBookingMessage = this.openBookingMessage.bind(this);
        this.closeBookingMessage = this.closeBookingMessage.bind(this);
        this.checkDatesForBooking = this.checkDatesForBooking.bind(this);
    }

    checkDatesForBooking() {
        var checkInDate = new Date(this.state.checkInDate);
        var checkOutDate = new Date(this.state.checkOutDate);
        if (checkInDate === checkOutDate) {
            return false;
        }
        if (checkInDate >= checkOutDate) {
            return false;
        }
        return true;
    }

    openBookingMessage() {
        this.setState({
            bookingMessageVisible: true
        });
    }

    closeBookingMessage() {
        this.setState({
            bookingMessageVisible: false
        });
    }

    componentDidMount(event) {
        HouseService.getHouseInfo(this.state.id, localStorage.getItem("jwt"))
            .then((response) => {
                this.setState({
                    houseInfo: response.data
                })
                console.log(this.state.houseInfo)
            })
            .catch((error) => {
                if (error.response.status === 404) {
                    this.setState({
                        exists: false
                    })
                }
            });


        HouseService.getHouseReviews(this.state.id)
            .then((response) => {
                if (response.status === 200) {
                    this.setState({
                        reviews: response.data
                    });
                    console.log(this.state.reviews)
                }
            })
            .catch((error) => {
                console.log(error.data);
            });
        HouseService.getHouseFacilities(this.state.id)
            .then((response) => {
                this.setState({
                    facilities: response.data
                });
            })
            .catch((error) => {
                console.log(error.data);
            })
    }

    componentDidUpdate(prevProps, prevState) {

        if (this.state.houseInfo && !prevState.houseInfo) {
            let imgs = [];
            for (let i = 0; i < this.state.houseInfo.houseImages.length; i++) {
                imgs[i] = getHouseImage(this.state.id, this.state.houseInfo.houseImages[i]);
            }
            let slidesImages = imgs.map((image) => {
                let temp = { "type": "img", "src": `${image}` }
                return temp;
            })
            rsl.init({
                "container_id": "houseSlider",
                "slides": slidesImages,
                "bullets": true,
                "arrows": true,
                "animation": "rsl_cursive",
                "autoplay": 6000,
            })

        }

    }

    handleChange(event) {
        event.preventDefault()
        this.setState({
            [event.target.name]: event.target.value, 
            isAvailable: false,
            requestSent: false
        });
    }

    verifyIfAvailable(event) {
        event.preventDefault();
        const isValid = this.checkDatesForBooking();
        if(isValid === false) {
            this.setState({
                validHouseMessage: "Check in date must be lower than check out date",
                requestSent: true
            })
            return;
        }
        const data = {
            checkInDate: this.state.checkInDate,
            checkOutDate: this.state.checkOutDate,
            houseID: this.state.id
        }
        HouseService.verifyAvailabilityDate(data, this.state.id)
            .then((response) => {
                console.log(response.data)
                if (response.status === 200) {
                    this.setState({
                        isAvailable: response.data.available,
                        noOfDays: response.data.noOfDays,
                        validHouseMessage: null
                    });
                }
                if (response.status === 204) {
                    this.setState({
                        isAvailable: false,
                        requestSent: true,
                        validHouseMessage: "House is not available"
                    })
                }
            })
            .catch((error) => {
                console.log(error.data);
            })

    }



    writeReview(event) {
        event.preventDefault();
        const jwt = localStorage.getItem("jwt");
        if (jwt === null) {
            this.props.history.push('/home');
            return;
        }
        const data = {
            reviewComment: this.state.reviewComment,
            reviewRating: this.state.reviewRating
        }
        HouseService.addNewReview(data, this.state.id, jwt)
            .then(() => {
                window.location.reload(false);
            })
            .catch((error) => {
                if (error.response.status === 401) {
                }
            });
    }

    bookHouse(event) {
        event.preventDefault();

        const isValid = this.checkDatesForBooking();
        if(isValid === false) {
            this.setState({
                validHouseMessage: "Check in date must be lower than check out date"
            })
            return;
        }

        const data = {
            checkInDate: this.state.checkInDate,
            checkOutDate: this.state.checkOutDate,
            houseID: this.state.id
        }
        HouseService.bookHouse(data, this.state.id, localStorage.getItem("jwt"))
            .then(() => {
                this.setState({
                    bookingStatus: "House booked successfully"
                })
                this.openBookingMessage();
            })
            .catch((error) => {
                if (error.response.status === 401) {
                    console.log(this.props.openLoginModal)
                    return this.props.openLoginModal();
                }
                if (error.response.status === 409) {
                    this.setState({
                        bookingStatus: "Error! House is already booked"
                    })
                    this.openBookingMessage();
                }
            })

    }

    onStarClick(nextValue, prevValue, name) {
        this.setState({ reviewRating: nextValue });
    }


    render() {


        let houseTitle = null;
        let houseImages = null;
        let houseDesc = null;
        let hostDesc = null;
        let verifyAvailability = null;
        let houseTitleReviews = null;
        let houseReviews = null;
        let writeReviewBox = null;
        let bookingMessage = null;

        if (this.state.houseInfo && this.state.exists) {
            houseTitle = <div className="housePTitleContainer">
                <div className="hTittleContainer">
                    {this.state.houseInfo.houseName}
                </div>
                <div className="subtitleLocation">
                    <img src={locationLogo} alt='' />
                    {this.state.houseInfo.streetName}, {this.state.houseInfo.city}, {this.state.houseInfo.country}
                </div>
            </div>

            const img = getHouseImage(this.state.houseInfo.houseId, this.state.houseInfo.mainImage);
            houseImages = <div className="houseSlider" id="houseSlider" />



            const hostImg = getUserImage(this.state.houseInfo.userID, this.state.houseInfo.photo);
            hostDesc = <div className="hostDesciption">
                <p>
                    This house is hosted by {this.state.houseInfo.lastName}
                </p>
                <img src={hostImg} alt='' />
            </div>

            const f = this.state.facilities.map((data) => {
                return (
                    <p>-{data}</p>
                )
            })
            houseDesc = <div className="housePDescription">
                {this.state.houseInfo.description}
                <div className="facilitiesContainer">
                    Most appreciated facilities: <br />
                    {f} <br />
                </div>
            </div>



            verifyAvailability = <div className="vAvailability">
                <div className="verifyContainerTop">
                    <div className="verifyContainerTitle">
                        Add dates for prices
                    </div>
                    <div className="verifyContainerRating">
                        <div className="vRating">
                            {this.state.houseInfo.rating.toFixed(1)}
                        </div>
                        <div className="vReviewNumber">
                            ({this.state.houseInfo.reviewNumber})
                        </div>
                    </div>
                </div>
                <div className="verifyDatesContainer">
                    <input name="checkInDate" type="date" value={this.state.checkInDate} onChange={this.handleChange}
                        style={{ borderTopLeftRadius: 15, borderBottomLeftRadius: 15 }}
                    />
                    <input name="checkOutDate" type="date" value={this.state.checkOutDate} onChange={this.handleChange}
                        style={{ borderTopRightRadius: 15, borderBottomRightRadius: 15, borderLeft: 0 }}
                    />
                </div>

                {
                    this.state.isAvailable ?
                        <>
                            <div className="calculatePriceContainer">
                                {this.state.houseInfo.costPerNight} lei x {this.state.noOfDays} nights
                            </div>
                            <div className="calculateTotalContainer">
                                Total {this.state.houseInfo.costPerNight * this.state.noOfDays} lei
                            </div>
                            <form onSubmit={this.bookHouse} >
                                <div className="verifyDatesButton">
                                    <button type="submit" style={{ paddingLeft: "10%", paddingRight: "10%" }}>Book</button>
                                </div>
                            </form>
                        </>
                        :
                        <>
                            <form onSubmit={this.verifyIfAvailable}>
                                <div className="verifyDatesButton">
                                    <button type="submit">Check availability</button>
                                </div>
                                <div className="notAvailableMessage">
                                    {
                                        this.state.requestSent ?
                                            <div> {this.state.validHouseMessage} </div>
                                            :
                                            <div> </div>
                                    }
                                </div>
                            </form>
                        </>
                }
            </div>

            if (this.state.reviews) {
                houseTitleReviews = <div className="">
                    <div className="reviewsTitle">
                        <div className="revRating">
                            {this.state.houseInfo.rating.toFixed(1)}
                        </div>
                        <div className="revNumber">
                            ({this.state.houseInfo.reviewNumber} reviews)
                        </div>
                    </div>
                </div>
                houseReviews = this.state.reviews.map((data) => {
                    const userImage = getUserImage(data.userID, data.userImage);
                    return (
                        <div className="oneReview">
                            <div className="singleReviewContainer">
                                <div>
                                    <img src={userImage} alt='' />

                                </div>
                                <div style={{ paddingLeft: "3%", paddingRight: "3%" }}>
                                    {data.lastName}
                                </div>
                                <div>
                                    <StarRatingComponent
                                        name="reviewRating"
                                        starCount={10}
                                        value={data.rating}
                                        disabled={true}
                                        starSize={30}
                                        caption="Disabled"
                                    />
                                </div>

                            </div>
                            <div className="reviewComment">
                                {data.comment}
                            </div>
                        </div>
                    )
                })
            }

            if (localStorage.getItem("jwt")) {
                writeReviewBox = <div className="">
                    <p>
                        Have you visited this house? Tell us about your experience now!
                    </p>
                    <div className="ratingStar">
                        What's your rating? <br />
                        <StarRatingComponent
                            name="reviewRatgin"
                            starCount={10}
                            value={this.state.reviewRating}
                            onStarClick={this.onStarClick}
                            starSize={30}
                        />
                    </div>
                    <form onSubmit={this.writeReview}>
                        <textarea name="reviewComment" placeholder="Write a review" required onChange={this.handleChange} />
                        <button type="submit">Add review</button>
                    </form>
                </div>
            } else {
                const openLoginModal = this.props.openLoginModal;
                writeReviewBox = <div className="logInForWriteReveiew">
                    <p>
                        Do you want to write a review? <a
                            onClick={openLoginModal} style={{ color: "blue", }}
                        >Log in</a> first!
                    </p>
                </div>
            }

            bookingMessage = <Modal
                visible={this.state.bookingMessageVisible}
                width="390"
                height="500"
                effect="fadeInUp"
                onClickAway={() => this.closeBookingMessage()}
            >
                <div className="loginContainer">
                    {this.state.bookingStatus}
                </div>
            </Modal>

        }





        return (
            <>
                <div className="allHouseConainter">
                    {houseTitle}
                    {houseImages}
                    {hostDesc}
                    <div className="descAndVerifyAvContainer">
                        {houseDesc}
                        {verifyAvailability}
                    </div>
                    <div className="allReviewsContainer">
                        {houseTitleReviews}
                        {houseReviews}
                    </div>
                    <div className="writeReviewContainer">
                        {writeReviewBox}
                    </div>
                </div>
                <Modal
                    visible={this.state.bookingMessageVisible}
                    width="350"
                    height="375"
                    effect="fadeInUp"
                    onClickAway={() => this.closeBookingMessage()}
                >
                    <div className="houseBookedNotification">
                        {this.state.bookingStatus} <br />
                        <button onClick={() => {window.location.reload();}}>
                            Back
                        </button>
                    </div>
                </Modal>

            </>
        )
    }

}

export default House;