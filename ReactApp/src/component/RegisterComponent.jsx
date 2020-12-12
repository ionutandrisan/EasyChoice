import React, { Component } from "react";
import PersonDataService from "../service/PersonDataService";
import '../css/Register.css';
import whiteLogo from '../images/whitelogo.PNG';

class RegisterComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {
            firstName: null,
            lastName: null,
            email: null,
            password: null,
            birthDate: "2000-01-01",
            phoneNumber: null,
            firstNameError: null,
            lastNameError: null,
            emailError: null,
            passwordError: null,
            birthDateError: null,
            phoneNumberError: null,
            registerMessage: null
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(event) {
        event.preventDefault()
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    validate() {
        let emailError = null;
        // eslint-disable-next-line
        const regex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
        if (!regex.test(this.state.email)) {
            emailError = 'Invalid email';
        } else {
            this.setState({ emailError: null });
        }

        if (emailError) {
            this.setState({ emailError });
            return false;
        }

        return true;
    }

    handleSubmit(event) {
        event.preventDefault();
        var isValid = this.validate()
        if (isValid) {
            const data = this.state
            PersonDataService.insertPerson(data)
                .then((response) => {
                    this.setState({
                        registerMessage: response.data
                    });
                    PersonDataService.loginUser({
                        email: this.state.email,
                        password: this.state.password
                    })
                        .then((response) => {
                            if (response.status === 200) {
                                localStorage.setItem("jwt", response.data.jwt)
                                localStorage.setItem("email", response.data.email)
                                this.props.history.push('/userinfo');
                            }
                        })
                        .catch(() => {});
                })
                .catch((error) => {
                    if (error.response) {
                        if (error.response.status === 409) {
                            console.log(error.response.data)
                            this.setState({
                                registerMessage: error.response.data
                            });
                        } else {
                            console.log("sad")
                        }
                    }
                });
        }
    }

    render() {

        const openLoginModal = this.props.openLoginModal;

        return (
            <div className="registerComponent">
                <div className="registerBlock">
                    <div className="registerTitle">
                        <img src={whiteLogo} alt='' />
                        <p>Account</p>
                    </div>
                    <div className="subtitleBlock">
                        <p>Create an account for quick use of SmartChoice services</p>
                    </div>
                    <form className="registerForm" onSubmit={this.handleSubmit}>
                        <p>Account details</p>
                        <input name="email" placeholder="e-mail" onChange={this.handleChange} required />
                        <div className="errorMessage"> {this.state.emailError}</div>
                        <input name="password" type="password" placeholder="Password" onChange={this.handleChange} required />
                        <div> {this.state.passwordError}</div>

                        <p>Personal details</p>
                        <input name="firstName" placeholder="First Name" onChange={this.handleChange} required />
                        <div> {this.state.firstNameError}</div>
                        <input name="lastName" placeholder="Last Name" onChange={this.handleChange} required />
                        <div> {this.state.lastNameError}</div>
                        <input name="phoneNumber" placeholder="Phone Number" onChange={this.handleChange} required />
                        <div> {this.state.phoneNumberError}</div>
                        <input name="birthDate" type="date" value={this.state.birthDate} onChange={this.handleChange} required />
                        <div> {this.state.birthDateError}</div>
                        <button type="submit">Register</button> <br />
                        <div className="errorMessage">{this.state.registerMessage}</div>
                        <div className="haveAccount">Already have an account?&nbsp;Go to <span onClick={openLoginModal}  >Log In</span></div>
                    </form>
                    
                </div>
            </div>
        );
    }
}

export default RegisterComponent