import { useContext } from "react";
import { MyUserContext } from "../configs/Context";
import { Button, Container, Nav, Navbar } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";

const Header = () => {
    const navigate = useNavigate();
    const [user, dispatch] = useContext(MyUserContext);

    return (
        <Navbar bg='primary' data-bs-theme='dark' expand="lg" className="bg-body-tertiary">
            <Container>
                <Navbar.Brand as={Link} to="/" className="fw-bold">Đông Thành</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ms-auto d-flex align-items-center">
                        {user ? (
                            <>
                                <Nav.Link as={Link} to="/profile" className="btn btn-outline-primary me-2">
                                    Thông tin cá nhân
                                </Nav.Link>
                                <Nav.Link
                                    as={Link}
                                    onClick={(e) => {
                                        e.preventDefault();
                                        dispatch({ type: "logout" });
                                        navigate("/login");
                                    }}
                                    className="btn btn-outline-danger"
                                >
                                    Đăng xuất
                                </Nav.Link>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/login">Đăng nhập</Nav.Link>
                                <Nav.Link as={Link} to="/register">Đăng ký</Nav.Link>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;