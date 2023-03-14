import React, {useEffect, useState} from "react";
import MaterialReactTable from "material-react-table";

function TransactionsTab() {
    const [transactions, setTransactions] = useState([]);
    const [transactionColumns, setTransactionColumns] = useState([]);
    useEffect(() => {
        fetch(process.env.REACT_APP_API_BASE + "/transaction/schema") // your url may look different
            .then(resp => resp.json())
            .then(data => {
                setTransactionColumns(data.map(it => {
                        return {
                            header: it.name,
                            accessorKey: it.name,
                            accessorFn: row => it.type.includes("Category") ? row.category.name : row[it.name]
                        }
                    })
                )
            }) // set data to state
    }, []);

    useEffect(() => {
        fetch(process.env.REACT_APP_API_BASE + "/transaction") // your url may look different
            .then(resp => resp.json())
            .then(data => setTransactions(data)) // set data to state
    }, [transactionColumns]);

    const [matchedTransactions, setMatchedTransactions] = useState([]);
    const [matchedTransactionColumns, setMatchedTransactionColumns] = useState([]);
    useEffect(() => {
        fetch(process.env.REACT_APP_API_BASE + "/transaction/matched/schema") // your url may look different
            .then(resp => resp.json())
            .then(data => {
                setMatchedTransactionColumns(data.map(it => {
                        return {
                            header: it.name,
                            accessorKey: it.name
                        }
                    })
                )
            }) // set data to state
    }, []);

    useEffect(() => {
        fetch(process.env.REACT_APP_API_BASE + "/transaction/matched") // your url may look different
            .then(resp => resp.json())
            .then(data => setMatchedTransactions(data)) // set data to state
    }, [matchedTransactionColumns]);



    return (
        <div className="TransactionsTab">
            <h1>Transactions</h1>
            <MaterialReactTable className="TransactionsTable"
                                columns={transactionColumns}
                                data={transactions}
                                enableRowSelection //enable some features
                                enableColumnOrdering
                                enableGlobalFilter={true} //turn off a feature
                                enableFullScreenToggle={false}
                                enableEditing={true}
                                enableRowVirtualization={true}
            />
            <h1>Matched Transactions</h1>
            <MaterialReactTable className="TransactionsTable"
                                columns={matchedTransactionColumns}
                                data={matchedTransactions}
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

export default TransactionsTab;