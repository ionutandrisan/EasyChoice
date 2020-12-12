function AddJwtToHeader(jwt) {
    var header = { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`
    }
    return header;
}

export default AddJwtToHeader;