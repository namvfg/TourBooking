import { useContext, useState } from "react";
import { MyUserContext } from "../configs/Context";
import { Button, Container, Nav, Navbar } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import ChatMessengerModal from "../provider/ChatMessengerModal";

const Header = () => {
    const navigate = useNavigate();
    const [user, dispatch] = useContext(MyUserContext);
    const [showChatModal, setShowChatModal] = useState(false);

    let chatMode = null, chatCurrentId = null;
    if (user) {
        if (user.role === "USER") {
            chatMode = "user";
            chatCurrentId = user.id;
        }
        if (user.role === "PROVIDER") {
            chatMode = "provider";
            chatCurrentId = user?.provider?.providerId;
        }
    }

    const handleBrandClick = (e) => {
        e.preventDefault();
        if (user?.role === "PROVIDER") {
            navigate("/provider-home");
        } else {
            navigate("/");
        }
    };

    return (
        <Navbar bg='primary' data-bs-theme='dark' expand="lg" className="bg-body-tertiary">
            <Container>
                <Navbar.Brand
                    href="#"
                    onClick={handleBrandClick}
                    className="fw-bold"
                >
                    Đông Thành
                </Navbar.Brand>

                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ms-auto d-flex align-items-center">
                        {user && chatMode && (
                            <>
                                <Button
                                    variant="outline-light"
                                    className="me-2"
                                    onClick={() => setShowChatModal(true)}
                                >
                                    <i className="bi bi-chat-dots"></i> Hộp thư
                                </Button>
                                <ChatMessengerModal
                                    show={showChatModal}
                                    onHide={() => setShowChatModal(false)}
                                    mode={chatMode}
                                    currentId={chatCurrentId}
                                    currentUser={user}
                                />
                            </>
                        )}

                        {user && user.role === "USER" && (
                            <Nav.Link as={Link} to="/user-transactions" className="btn btn-outline-warning me-2">
                                <i className="bi bi-receipt"></i> Lịch sử giao dịch
                            </Nav.Link>
                        )}

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
