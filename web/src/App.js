import * as React from "react";
import {Routes, Route} from "react-router-dom";
import './App.css';
import {Home} from "./screens/home/Home";
import Admin from "./screens/admin/Admin";
import List from "./screens/admin/list/List";
import Create from "./screens/admin/create/Create";
import {Container, Nav, Navbar, NavbarBrand, NavLink} from "react-bootstrap";
import NavbarToggle from "react-bootstrap/NavbarToggle";
import NavbarCollapse from "react-bootstrap/NavbarCollapse";

function App() {
    return (
        <>
            <header>
                <Navbar variant="dark" bg="dark" className="shadow-sm">
                    <Container>
                        <NavbarBrand href="/"><strong>SO5</strong></NavbarBrand>
                        <NavbarToggle aria-controls="header-navbar-nav" />
                        <NavbarCollapse id="header-navbar-nav">
                            <Nav className="me-auto">
                                <NavLink href="/admin">Admin</NavLink>
                            </Nav>
                        </NavbarCollapse>
                    </Container>
                </Navbar>
            </header>
            <>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/admin" element={<Admin/>}>
                        <Route index element={<List/>}/>
                        <Route path="create" element={<Create/>}/>
                        <Route path=":sku" element={<Create/>}/>
                    </Route>
                </Routes>
            </>
        </>
    );
}

export default App;
