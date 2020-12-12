import photoSrc from "../lib/GetUserImageURL";
import HouseService from '../service/HouseService';

function getUserImage(houseID, imageName) {
    if(imageName === null) {
        houseID = 0;
        imageName = 'default.jpg';
    }
    const imageSrc = photoSrc(houseID, imageName);
    HouseService.getImageURL(imageSrc)
    .then(() =>{
        
    })
    .catch(() => {
        
    })
    return imageSrc;
}

export default getUserImage;