import { API } from "../constants/api";

function photoSrc(id, image) {
    return `${API}/userimage/${id}/${image}`;
}

export default photoSrc;