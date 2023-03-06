import { Tab, Tabs, TabList, TabPanel } from "react-tabs";
import './App.css';
import TransactionsTab from './Components/TransactionsTab';
import CategoriesTab from './Components/CategoriesTab';
import VendorsTab from './Components/VendorsTab';


function App() {
  return (
    <div className="App">
        <header className="App-header">
            <h1>Finance Tool Web Interface</h1>
        </header>
            <Tabs className="Tabs">
                <TabList>
                    <Tab>Transactions</Tab>
                    <Tab>Categories</Tab>
                    <Tab>Vendors</Tab>
                </TabList>
                <TabPanel>
                    <TransactionsTab />
                </TabPanel>
                <TabPanel>
                    <CategoriesTab />
                </TabPanel>
                <TabPanel>
                    <VendorsTab />
                </TabPanel>
            </Tabs>
    </div>
  );
}

export default App;
