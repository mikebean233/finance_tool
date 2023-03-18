import React, { useEffect, useState } from "react";
import MaterialReactTable from 'material-react-table';
import { getApiPrefix } from "../Common";

function CategoriesTab() {
    const [categories, setCategories] = useState([]);
    const [columns, setColumns] = useState([]);
    useEffect(() => {
        fetch(`${getApiPrefix()}/category/schema`) // your url may look different
            .then(resp => resp.json())
            .then(data => {
                setColumns(data.map(it => {return {header: it.name, accessorKey: it.name }})
                 )
            }) // set data to state
    }, []);

    useEffect(() => {
        fetch(`${getApiPrefix()}/category`) // your url may look different
            .then(resp => resp.json())
            .then(data => setCategories(data)) // set data to state
    }, [columns]);

    return (
        <div className="CategoriesTab">
            <h1>Categories</h1>
            <MaterialReactTable className="CategoriesTable"
                columns={columns}
                data={categories}
                enableRowSelection //enable some features
                enableColumnOrdering
                enableGlobalFilter={true} //turn off a feature
                enableFullScreenToggle={false}
                enableEditing={true}
                enableRowVirtualization={true}
            />
        </div>
    );
}

export default CategoriesTab;