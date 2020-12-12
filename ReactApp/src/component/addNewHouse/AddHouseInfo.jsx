import React, { Component } from 'react';
import HouseService from '../../service/HouseService';
import '../../css/AddHouseInfo.css';
import whiteLogo from '../../images/whitelogo.PNG';

class AddHouseInfo extends Component {

    constructor(props) {
        super(props);
        this.state = {
            country: null,
            city: null,
            streetName: null,
            houseName: null,
            description: null,
            costPerNight: null,
            housePhotos: null,
            facilities: null,
            facilitiesCheckedHash: null,
            photoError: null,
            priceError: null,

        }

        this.componentDidMount = this.componentDidMount.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handlePhotoChange = this.handlePhotoChange.bind(this);
        this.getFacilitiesForHouse = this.getFacilitiesForHouse.bind(this);
        this.handleCheckBoxChange = this.handleCheckBoxChange.bind(this);
        this.handleCheckBoxChange = this.handleCheckBoxChange.bind(this);
        this.getCheckedFacilities = this.getCheckedFacilities.bind(this);
        this.validateIfImages = this.validateIfImages.bind(this);
        this.checkHouseDetails = this.checkHouseDetails.bind(this);
    }

    checkHouseDetails() {

        if (isNaN(this.state.costPerNight)) {
            this.setState({
                priceError: 'Price must be a number'
            })
            return false;
        }
        if (this.state.costPerNight < 0 || this.state.costPerNight > 999999) {
            this.setState({
                priceError: 'Price must be between 1 and 99 999 lei'
            })
            return false;
        }

        return true;
    }

    getFacilitiesForHouse(facilities) {
        let houseFacilitiesHashMap = new Map();
        for (let i = 0; i < facilities.length; i++) {
            houseFacilitiesHashMap.set(facilities[i], false);
        }
        return houseFacilitiesHashMap;
    }

    componentDidMount(event) {
        HouseService.getFacilities()
            .then((response) => {
                this.setState({
                    facilities: response.data,
                    facilitiesCheckedHash: this.getFacilitiesForHouse(response.data)
                });

            })
            .catch(() => { })
    }

    handleChange(event) {
        event.preventDefault();
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    getCheckedFacilities() {
        let facilitiesToAdd = [];
        for (let [key, value] of this.state.facilitiesCheckedHash) {
            console.log(key + ' = ' + value)
            if (value === true) {
                facilitiesToAdd.push(key);
            }
        }
        return facilitiesToAdd;
    }

    handleCheckBoxChange(event) {
        let hm = this.state.facilitiesCheckedHash;
        hm.set(event.target.name, event.target.checked);
        this.setState({
            facilitiesCheckedHash: hm
        });
    }


    handleSubmit(event) {
        event.preventDefault();
        const isValid = this.checkHouseDetails();
        if (isValid) {
            const jwt = localStorage.getItem("jwt");
            if (jwt !== null) {
                let facilities = this.getCheckedFacilities();
                let formData = new FormData();
                if (this.state.housePhotos) {
                    for (let i = 0; i < this.state.housePhotos.length; i++) {
                        formData.append('houseImages', this.state.housePhotos[i]);
                    }
                }
                formData.append('country', this.state.country);
                formData.append('city', this.state.city);
                formData.append('streetName', this.state.streetName);
                formData.append('costPerNight', this.state.costPerNight);
                formData.append('houseName', this.state.houseName);
                formData.append('description', this.state.description);
                formData.append('facilities', facilities);
                HouseService.setHouseInfo(jwt, formData)
                    .then((response) => {
                        if (response.status === 200) {
                            this.props.history.push('/home');
                        }
                    })
                    .catch((error) => {
                        if (error.response.status === 400) {
                            this.setState({
                                addHouseError: "Failed to add house"
                            })
                        }
                    })
            } else {
                this.props.history.push('/login');
            }
        }
    }

    validateIfImages(event) {
        for (let i = 0; i < event.target.files.length; i++) {
            if (!event.target.files[i].type.startsWith("image")) {
                return false;
            }
        }
        return true;
    }

    handlePhotoChange(event) {
        event.preventDefault();
        const isValid = this.validateIfImages(event);
        if (isValid) {
            this.setState({
                housePhotos: event.target.files,
                photoError: null
            });

        } else {
            event.target.value = null;
            this.setState({
                photoError: "Files must have .jpg/.png extension",
                housePhotos: null
            });
        }
    }

    render() {

        let images;
        let fac;
        if (this.state.housePhotos) {
            const img = Array.from(this.state.housePhotos);
            images = img.map((image) =>
                <img src={URL.createObjectURL(image)} alt="" />
            );
        }

        if (this.state.facilities) {
            fac = this.state.facilities.map((facility) =>
                <div className="facilityContainer">
                    <input name={facility} type="checkbox" onChange={this.handleCheckBoxChange} />
                    {facility}
                </div>
            );
        }




        return (
            <div className="houseInfoComponent">
                <div className="houseInfoBlock">

                    <div className="addHouseLogo">
                        <img src={whiteLogo} alt='' />
                    </div>

                    <div className="addHouseTitle">
                        Do you want to become a host? Add your house on SmartChoice.com now!
                    </div>

                    <form onSubmit={this.handleSubmit}>
                        <div className="houseDescription">
                            First add a description and name of the house
                        </div>
                        <input name="houseName" placeholder="Add a name" onChange={this.handleChange} required /> <br />
                        <textarea name="description" placeholder="Add a description" onChange={this.handleChange} required /> <br />

                        <div className="houseDescription">
                            Now let's add the location of the house
                        </div>
                        <input name="country" placeholder="Add country" onChange={this.handleChange} required /> <br />
                        <input name="city" placeholder="Add city" onChange={this.handleChange} required /> <br />
                        <input name="streetName" placeholder="Add street name" onChange={this.handleChange} required /> <br />


                        <div className="houseDescription">
                            Let's choose a fair price (per night)
                        </div>
                        <input name="costPerNight" placeholder="Choose a price" onChange={this.handleChange} required /> <br />
                        <div style={{ color: "red", textAlign: "center" }}>{this.state.priceError}</div>

                        <div className="houseDescription">
                            Now let's add some photos
                        </div>
                        <div className="addHousePhotosButton">
                            <label htmlFor="housePhotos">
                                <p>Upload photos</p>
                            </label>
                        </div>
                        <input name="housePhotos" id="housePhotos" type="file" accept="image/*" multiple onChange={this.handlePhotoChange}
                            style={{ width: "0px", height: "0px" }}
                        /> <br />
                        <div style={{ color: "red" }}>{this.state.photoError}</div>
                        <div class="addedImagesBlock">{images}</div>

                        <div className="houseDescription">
                            Finally, let your customers know which facilities can you provide
                        </div>
                        <div className="facilityBlock">
                            {fac}
                        </div>

                        <button type="submit">Add your house</button> <br />
                        <div className="errorMessage">{this.state.addHouseError}</div>

                    </form>
                </div>
            </div>
        );
    }

}

export default AddHouseInfo;