export default function getCurrentDate(dayDiff) {
    let newDate = new Date();
    let day = newDate.getDate() + dayDiff;
    let month = newDate.getMonth();
    let year = newDate.getFullYear();

    if(month < 10) {
        month = '0' + month;
    }

    return `${year}-${month}-${day}`;
}