import { Form, Button, Card, Container } from "react-bootstrap";
import { useContext, useState } from "react";
import cookie from "react-cookies";
import { Link, useNavigate } from "react-router-dom";
import MySpinner from "../layout/MySpinner";
import MyAlert from "../layout/MyAlert";
import Apis, { authApis, endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/Context";

const Login = () => {
    const [username, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const [, dispatch] = useContext(MyUserContext);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const res = await Apis.post(endpoints.login, { username, password });
            const { token, error } = res.data || {};

            if (!token) {
                setError(error || "Đăng nhập thất bại!");
                return;
            }
            cookie.save("token", token, { path: "/" });
            const profileRes = await authApis(token).get(endpoints["profile"]);
            dispatch({ type: "login", payload: profileRes.data });
            navigate("/");
        } catch (err) {
            console.error(err);
            setError("Lỗi hệ thống hoặc sai tài khoản/mật khẩu!");
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <MySpinner />;

    return (
        <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: "70vh" }}>
            <Card style={{ width: "100%", maxWidth: "400px" }} className="p-4 shadow">
                <h3 className="mb-4 text-center">Đăng nhập</h3>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3" controlId="formUsername">
                        <Form.Label>Username</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="Nhập username"
                            value={username}
                            onChange={(e) => setUserName(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-4" controlId="formPassword">
                        <Form.Label>Mật khẩu</Form.Label>
                        <Form.Control
                            type="password"
                            placeholder="Nhập mật khẩu"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Button type="submit" variant="primary" className="w-100">
                        Đăng nhập
                    </Button>

                    <MyAlert message={error} onClose={() => setError("")} />
                </Form>

                <div className="mt-3 text-center">
                    Chưa có tài khoản? <Link to="/register">Đăng ký</Link>
                </div>
            </Card>
        </Container>
    );
};

export default Login;
