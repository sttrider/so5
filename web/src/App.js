import * as React from "react";
import {Routes, Route} from "react-router-dom";
import './App.css';
import {Home} from "./screens/home/Home";
import Admin from "./screens/admin/Admin";
import List from "./screens/admin/list/List";
import Create from "./screens/admin/create/Create";

function App() {
    return (
        <div className="App">
            <h1>SO5</h1>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/admin" element={<Admin/>}>
                    <Route index element={<List/>}/>
                    <Route path="create" element={<Create/>}/>
                </Route>
            </Routes>
        </div>
    );
}

export default App;
