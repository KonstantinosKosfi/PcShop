const Server = "http://localhost:8080/api";

export const fetchProductByUUid = async (userInput) => {
    try {
        const response = await fetch(Server + `/product/search-uuid?uuid=${userInput}`);
        const data = await response.json();
        return {data, status: response.status};
    } catch (error) {
    }
};

export const fetchProductByCategory = async (category) => {
    try {
        const response = await fetch(Server + `/product/search-category?category=${category}`);
        const data = await response.json();
        return {data, status: response.status};
    } catch (error) {
    }
}

export const fetchProductByProductName = async (productName) => {
    try {
        const response = await fetch(Server + `/product/search-name?name=${productName}`);
        const data = await response.json();
        return {data, status: response.status};
    } catch (error) {
    }
}
