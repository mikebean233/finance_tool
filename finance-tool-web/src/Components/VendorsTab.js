import React, {useEffect, useState} from "react";
import MaterialReactTable from "material-react-table";
import {getApiPrefix} from "../Common";

function VendorsTab() {
    const [vendors, setVendors] = useState([]);
    const [columns, setColumns] = useState([]);
    useEffect(() => {
        fetch(`${getApiPrefix()}/vendor/schema`) // your url may look different
            .then(resp => resp.json())
            .then(data => {
                setColumns(data.map(it => {
                        return {header: it.name, accessorKey: it.name, accessorFn: row => it.type.includes("Category") ? row.category.name : row[it.name]}
                    })
                )
            }) // set data to state
    }, []);

    useEffect(() => {
        fetch(`${getApiPrefix()}/vendor`) // your url may look different
            .then(resp => resp.json())
            .then(data => setVendors(data)) // set data to state
    }, [columns]);

    return (
        <div className="VendorsTab">
            <h1>Vendors</h1>
            <MaterialReactTable className="VendorsTable"
                                columns={columns}
                                data={vendors}
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

export default VendorsTab;