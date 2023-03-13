import React, { useEffect, useState } from "react";

function CategoriesTable() {
    const [categories, setCategories] = useState([]);
    useEffect(() => {
        fetch("http://localhost:8080/") // your url may look different
            .then(resp => resp.json())
            .then(data => setCategories(data)) // set data to state
    }, []);

    return (
        <div className="CategoriesTab">
            <h1>Categories</h1>
        </div>
    );
}

export default CategoriesTable;