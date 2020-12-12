import React, { Component } from "react";
import PersonDataService from '../service/PersonDataService';

import '../css/MenuBar.css';
import logo from '../images/logoo.PNG';
import whiteLogo from '../images/whitelogo.PNG'
import Modal from 'react-awesome-modal';

class MenuBar extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isAuth: null,
            visible: props.visible,
            email: null,
            password: null,
            jwt: null,
            loginResponse: null,
        }
        this.componentDidMount = this.componentDidMount.bind(this);
        this.logOut = this.logOut.bind(this);
        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
        this.goToRegister = this.goToRegister.bind(this);
        this.showDropdownMenu = this.showDropdownMenu.bind(this);
        this.hideDropdownMenu = this.hideDropdownMenu.bind(this);
    }

    componentWillReceiveProps(nextProps) {
        this.setState({visible: nextProps.visible})
      }

    componentDidMount() {
        this.setState({ isAuth: Boolean(localStorage.getItem("jwt")) });
    }

    logOut() {
        this.setState({ isAuth: false })
        localStorage.clear();
        window.location.reload();
    }

    openModal() {
        this.setState({
            visible: true
        });
    }

    closeModal() {
        this.setState({
            visible: false
        });
    }

    handleChange(event) {
        event.preventDefault();
        this.setState({
            [event.target.name]: event.target.value
        });
    }

    handleLogin(event) {
        event.preventDefault();
        const data = {
            email: this.state.email,
            password: this.state.password
        };
        PersonDataService.loginUser(data)
            .then((response) => {
                if (response.status === 200) {
                    this.setState({ jwt: response.data.jwt })
                    localStorage.setItem("jwt", response.data.jwt)
                    localStorage.setItem("email", response.data.email)
                    this.closeModal();
                    window.location.reload();
                }
            })
            .catch((error) => {
                if (error.response.status === 401)
                    this.setState({ loginResponse: error.response.data })
            })
    }

    goToRegister() {
        this.props.history.push('/register');
    }

    showDropdownMenu(event) {
        event.preventDefault();
        this.setState({ displayMenu: true }, () => {
            document.addEventListener('click', this.hideDropdownMenu);
        });
    }

    hideDropdownMenu() {
        this.setState({ displayMenu: false }, () => {
            document.removeEventListener('click', this.hideDropdownMenu);
        });

    }

    render() {

        return (
            <div className="containerMenu">
                <a href="/home">
                    <img src={logo} alt="" />
                </a>
                {
                    this.state.isAuth ?
                        <>
                            <button onClick={() => this.logOut()}>Log Out</button>
                            <a href='/userinfo'>
                                <button>My Details</button>
                            </a>
                        </>
                        :
                        <>
                            <button onClick={() => this.openModal()}>Log In</button>
                            <a href='/register'>
                                <button >Register</button>
                            </a>
                            <Modal
                                visible={this.state.visible}
                                width="390"
                                height="500"
                                effect="fadeInUp"
                                onClickAway={() => this.closeModal()}
                            >
                                <div className="loginContainer">
                                    <div className="loginTitle">
                                        <img src={whiteLogo} alt='' />
                                        <p>Account</p>
                                    </div>
                                    <div className="loginText">
                                        Login
                                    <p>You can log in using your email for using our services</p> <br />
                                    </div>
                                    <form onSubmit={this.handleLogin}>
                                        <input name="email" placeholder="e-mail" onChange={this.handleChange} required /> <br />
                                        <input name="password" type="password" placeholder="Password" onChange={this.handleChange} required />  <br />
                                        <input type="submit" value="Login" />
                                        <div className="errorMessage">{this.state.loginResponse}</div>
                                    </form>
                                    <div className="noAccountContainer">
                                        Don't have an account?&nbsp;
                                        <a href='/register'>Create one</a>
                                    </div>
                                </div>
                            </Modal>
                        </>
                }
            </div>
        )

    }

}

export default MenuBar;