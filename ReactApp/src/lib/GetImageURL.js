import { API } from "../constants/api";

function photoSrc(id, image) {
    return `${API}/houseimg/${id}/${image}`;
}

export default photoSrc;