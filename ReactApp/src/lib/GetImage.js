import photoSrc from "../lib/GetImageURL";
import HouseService from '../service/HouseService';

function getImageFromURL(houseID, imageName) {
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

export default getImageFromURL;
