import { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../configs/Context";
import { Button, Container, Form, Nav, Navbar, NavDropdown, } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Apis";
import MySpinner from "./MySpinner";
import { Link, useLocation, useNavigate } from "react-router-dom";

const Header = () => {

    const location = useLocation();
    const navigate = useNavigate();

    const [user, dispatch] = useContext(MyUserContext);
    const [kw, setKw] = useState("");

    const handleSearch = (e) => {
        e.preventDefault();
        const searchParams = new URLSearchParams(location.search);
        if (kw.trim()) {
            searchParams.set("kw", kw);
        } else {
            searchParams.delete("kw");
        }
        searchParams.set("page", "1"); // Reset to page 1 on new search
        navigate(`${location.pathname}?${searchParams.toString()}`);
        setKw("");
    };

    return (
        <>
            <Navbar bg='primary' data-bs-theme='dark' expand="lg" className="bg-body-tertiary">
                <Container>
                    <Navbar.Brand as={Link} to="/">Đông Thành</Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav">
                        <Nav className="me-auto">
                            <NavDropdown title="Danh mục" id="basic-nav-dropdown">
                                <NavDropdown.Item href="#link" >

                                </NavDropdown.Item>
                            </NavDropdown>
                            {user ? (
                                <>
                                    <Nav.Link as={Link} to="/profile" className="btn btn-outline-primary me-2">Thông tin cá nhân</Nav.Link>
                                    <Nav.Link as={Link}
                                        onClick={(e) => {
                                            e.preventDefault();
                                            dispatch({ type: "logout" });
                                            navigate("/login");
                                        }}
                                        className="btn btn-outline-danger">Đăng xuất</Nav.Link>
                                </>
                            ) : (
                                <>
                                    <Nav.Link as={Link} to="/login">Đăng nhập</Nav.Link>
                                    <Nav.Link as={Link} to="/register">Đăng ký</Nav.Link>
                                </>
                            )}

                        </Nav>

                        <Form onSubmit={handleSearch} className="d-flex">
                            <Form.Control
                                type="search"
                                placeholder="Search"
                                className="me-2"
                                aria-label="Search"
                                name="kw"
                                value={kw}
                                onChange={(e) => setKw(e.target.value)}
                            />
                            <Button type="submit" variant="outline-primary">Tìm</Button>
                        </Form>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    );
}

export default Header;