import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import UserInfo from './UserInfo';
import RegisterComponent from './RegisterComponent';
import MenuBar from './MenuBar';
import AddHouseInfo from './addNewHouse/AddHouseInfo';
import House from "./House";
import Home from "./Home";
import SearchHouses from './SearchHouses';
import Statistics from './Statistics';
import HouseStatistics from './HouseStatistics';

class InstructorApp extends Component {

    constructor(props) {
        super(props)
        this.state = {
          visible: false,
        }
        this.openLoginModal = this.openLoginModal.bind(this);
      }
    
      openLoginModal() {
        this.setState({
          visible: true
        })
      }

    render() {
        return (
            <Router>
                <>
                    <MenuBar visible={this.state.visible} />
                    <Switch>
                        <Route path="/userinfo" exact component={() => <UserInfo openLoginModal={this.openLoginModal} />} />
                        <Route path="/register" exact component={() => <RegisterComponent openLoginModal={this.openLoginModal} />} />
                        <Route path="/addHouse/houseInfo" exact component={AddHouseInfo} />
                        <Route path="/house/:id" component={(routerProps) => <House {...routerProps} openLoginModal={this.openLoginModal} />} />
                        <Route path="/" exact component={Home} />
                        <Route path="/home" exact component={Home} />
                        <Route path="/searchHouse/:city" exact component={SearchHouses} />
                        <Route path="/mystatistics" exact component={Statistics} />
                        <Route path="/statistics/:id" exact component={HouseStatistics} />
                    </Switch>
                </>
            </Router>
        );
    }
}

export default InstructorApp