
import React, { Component } from 'react';
import { Bar, Line } from 'react-chartjs-2';
import '../css/Statistics.css';
import PersonDataService from '../service/PersonDataService';


class Statistics extends Component {

    constructor(props) {
        super(props);
        this.state = {
            lastName: null,
            noClientsGraphData: null,
            housesName: null,
            nrReviews: null,
            reviewRatings: null
        }
        this.getNoClientsGraph = this.getNoClientsGraph.bind(this);
        this.getNoClientsDataForGraph = this.getNoClientsDataForGraph.bind(this);
        this.getEarningsData = this.getEarningsData.bind(this);
        this.getReviewNumber = this.getReviewNumber.bind(this);
        this.getReviewRatginsForStatisticsGraph = this.getReviewRatginsForStatisticsGraph.bind(this);
        this.getGraphData = this.getGraphData.bind(this);
        this.getHousesName = this.getHousesName.bind(this);
        this.getNumberOfReviews = this.getNumberOfReviews.bind(this);
        this.getReviewRatingsForUser = this.getReviewRatingsForUser.bind(this);
    }

    componentDidMount() {
        const jwt = localStorage.getItem("jwt");
        if (jwt) {
            PersonDataService.getName(jwt)
                .then((response) => {
                    this.setState({
                        lastName: response.data
                    })
                })
                .catch((err) => {
                    console.log(err.data);
                });
            this.getNoClientsGraph(jwt);
            this.getHousesName(jwt);
            this.getNumberOfReviews(jwt);
            this.getReviewRatingsForUser(jwt);
        } else {
            this.props.history.push(`/home`);
        }
    }

    getGraphData(data) {
        const nrOfMonths = Object.keys(data).length;
        let graphValues = new Array(nrOfMonths).fill(0);
        for (let i = 0; i < nrOfMonths; i++) {
            graphValues[i] = data[i + 1];
        }
        return graphValues;
    }

    getNoClientsDataForGraph() {
        return this.getGraphData(this.state.noClientsGraphData.clientsHashMap);
    }

    getEarningsData() {
        return this.getGraphData(this.state.noClientsGraphData.earningsPerMonth);
    }

    getReviewNumber() {
        return this.getGraphData(this.state.nrReviews);
    }

    getReviewRatginsForStatisticsGraph() {
        let ratings = [];
        if (this.state.reviewRatings) {
            ratings[0] = this.state.reviewRatings[0];
            for (let i = 1; i < this.state.reviewRatings.length; i++) {
                ratings[i] = (ratings[i - 1] + this.state.reviewRatings[i]) / 2;
            }
        }
        console.log(ratings);
        return ratings;
    }

    getNoClientsGraph(jwt) {
        PersonDataService.getNoClientsForGraph(jwt)
            .then((response) => {
                this.setState({
                    noClientsGraphData: response.data
                })
                console.log(this.state.noClientsGraphData);
            })
            .catch((err) => {
                console.log(err.data);
            });
    }

    getHousesName(jwt) {
        PersonDataService.getHousesName(jwt)
            .then((response) => {
                this.setState({
                    housesName: response.data
                });
                console.log(this.state.housesName);
            })
            .catch((err) => {
                console.log(err.data);
            })
    }

    getNumberOfReviews(jwt) {
        PersonDataService.getHousesReviews(jwt)
            .then((response) => {
                this.setState({
                    nrReviews: response.data
                });
                console.log(this.state.nrReviews);
            })
            .catch((err) => {
                console.log(err.data);
            })
    }

    getReviewRatingsForUser(jwt) {
        PersonDataService.getReviewRatingsForUser(jwt)
            .then((response) => {
                this.setState({
                    reviewRatings: response.data
                });
                console.log(this.state.reviewRatings);
            })
            .catch((err) => {
                console.log(err.data);
            })
    }


    render() {

        let title = null;
        let clientsNumberGraph = null;
        let earningsPerMonth = null;
        let statisticsMenu = null;
        let numberOfReveiwsPerMonth = null;
        let ratings = null;


        if (localStorage.getItem("jwt") && this.state.noClientsGraphData && this.state.nrReviews && this.state.reviewRatings) {

            title = <div className="statisticsTitile">
                Hello, Mr/Ms {this.state.lastName}. Here is your summary for year 2020
            </div>

            if (this.state.housesName) {
                const housesNameForLabel = this.state.housesName.map((data) => {
                    return (
                        <li>
                            <a href={`/statistics/${data.houseID}`}>
                                {data.houseName}
                            </a>
                        </li>
                    )
                });
                statisticsMenu = <div className="statisticsMenuItems">
                    <p>
                        See statistics for
                </p>
                    <ul style={{ paddingInlineStart: "0px" }}>
                        <li>
                            <a href="/mystatistics">
                                All houses
                        </a>
                        </li>
                        {housesNameForLabel}
                    </ul>
                </div>
            }

            const nrClients = this.getNoClientsDataForGraph().reduce((a, b) => a + b, 0);
            const noClientsState = {
                labels: ['January', 'February', 'March',
                    'April', 'May', 'June', 'July'],
                datasets: [
                    {
                        label: 'Clients',
                        backgroundColor: 'rgba(75,192,192,1)',
                        borderColor: 'rgba(0,0,0,1)',
                        borderWidth: 2,
                        data: this.getNoClientsDataForGraph()
                    }
                ]
            }

            clientsNumberGraph = <div className="clientPerMonthGraph">
                <Bar
                    data={noClientsState}
                    options={{
                        title: {
                            display: true,
                            text: 'Clients per mounth in 2020',
                            fontSize: 20
                        },
                        legend: {
                            display: true,
                            position: 'right'
                        },
                        scales: {
                            yAxes:[{
                                ticks: {
                                    beginAtZero: true
                                }
                            }]
                        }
                    }}
                />
                <p>
                    In 2020, you hosted a number of {nrClients} clients.
                </p>
            </div>

            const earnings = this.getEarningsData().reduce((a, b) => a + b, 0);
            const clientEarnings = {
                labels: ['January', 'February', 'March',
                    'April', 'May', 'June', 'July'],
                datasets: [
                    {
                        label: 'lei',
                        fill: false,
                        lineTension: 0.25,
                        backgroundColor: 'rgba(75,192,192,1)',
                        borderColor: 'rgba(0,0,0,1)',
                        borderWidth: 2,
                        data: this.getEarningsData()
                    }
                ]
            }
            earningsPerMonth = <div className="clientPerMonthGraph">
                <Line
                    data={clientEarnings}
                    options={{
                        title: {
                            display: true,
                            text: 'Earnings per mounth in 2020 (lei)',
                            fontSize: 20
                        },
                        legend: {
                            display: true,
                            position: 'right'
                        },
                        scales: {
                            yAxes: [{
                                ticks: {
                                    suggestedMin: 0
                                }
                            }]
                        }
                    }}
                />
                <p>
                    In 2020, your earnings were: {earnings} lei.
                </p>
            </div>

            const reviewNumbers = {
                labels: ['January', 'February', 'March',
                    'April', 'May', 'June', 'July'],
                datasets: [
                    {
                        label: 'Reviews',
                        fill: false,
                        lineTension: 0.25,
                        backgroundColor: '#8c0060',
                        borderColor: 'rgba(0,0,0,1)',
                        borderWidth: 2,
                        data: this.getReviewNumber()
                    }
                ]
            }
            numberOfReveiwsPerMonth = <div className="reviewsPerMounthGraph">
                <Bar
                    data={reviewNumbers}
                    options={{
                        title: {
                            display: true,
                            text: 'Reviews per mounth in 2020',
                            fontSize: 20
                        },
                        legend: {
                            display: true,
                            position: 'right'
                        },
                        scales: {
                            yAxes:[{
                                ticks: {
                                    beginAtZero: true
                                }
                            }]
                        }
                    }}
                />

            </div>

            const vect = this.state.reviewRatings.map((data, index) => {
                return index + 1;
            })
            const housesRatings = {
                labels: vect,
                datasets: [
                    {
                        label: 'stars',
                        fill: false,
                        lineTension: 0.25,
                        backgroundColor: 'rgba(75,192,192,1)',
                        borderColor: 'rgba(0,0,0,1)',
                        borderWidth: 2,
                        data: this.getReviewRatginsForStatisticsGraph()
                    }
                ]
            }
            ratings = <div className="clientPerMonthGraph">
                <Line
                    data={housesRatings}
                    options={{
                        title: {
                            display: true,
                            text: 'Rating',
                            fontSize: 20
                        },
                        legend: {
                            display: true,
                            position: 'right'
                        },
                        scales: {
                            yAxes: [{
                                ticks: {
                                    suggestedMin: 0,
                                    suggestedMax: 10
                                }
                            }]
                        }
                    }}
                />
            </div>

        }

        return (
            <div style={{ height: "100%" }}>
                <div className="entrieClassContainer">
                    {title}
                    <div className="menuAndStatisticsContainer">
                        {statisticsMenu}
                        <div className="firstTwoGraphs">
                            {clientsNumberGraph}
                            {earningsPerMonth}
                            {numberOfReveiwsPerMonth}
                            {ratings}
                        </div>
                    </div>
                </div>
            </div>
        );
    }


}

export default Statistics;