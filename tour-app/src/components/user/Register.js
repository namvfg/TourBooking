import { useState, useMemo } from "react";
import { Form, Button, Card, Container, Row, Col } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import MySpinner from "../layout/MySpinner";
import MyAlert from "../layout/MyAlert";
import Apis, { endpoints } from "../configs/Apis";

const EMAIL_PATTERN = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
const PHONE_PATTERN = /^\d{10}$/;
const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/;

const Register = () => {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        email: "",
        username: "",
        password: "",
        confirmPassword: "",
        address: "",
        phoneNumber: "",
    });

    const [avatarFile, setAvatarFile] = useState(null);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleAvatarChange = (e) => {
        setAvatarFile(e.target.files[0]);
    };

    const validate = useMemo(
        () => () => {
            if (!form.firstName.trim() || !form.lastName.trim()) {
                return "Họ và tên không được để trống!";
            }
            if (!EMAIL_PATTERN.test(form.email)) {
                return "Email không hợp lệ!";
            }
            if (!PHONE_PATTERN.test(form.phoneNumber)) {
                return "Số điện thoại phải gồm đúng 10 chữ số!";
            }
            if (form.username.length < 3 || form.username.length > 20) {
                return "Username phải dài từ 3 đến 20 ký tự!";
            }
            if (!PASSWORD_PATTERN.test(form.password)) {
                return "Mật khẩu phải từ 6 ký tự, chứa cả chữ và số, và chỉ gồm chữ & số!";
            }
            if (form.password !== form.confirmPassword) {
                return "Mật khẩu không khớp!";
            }
            if (!avatarFile) {
                return "Vui lòng chọn ảnh đại diện!";
            }
            return "";
        },
        [form, avatarFile]
    );

    const handleRegisterRequest = async (e) => {
        e.preventDefault();
        if (loading) return;
        setError("");

        const err = validate();
        if (err) {
            setError(err);
            return;
        }

        setLoading(true);
        try {
            const formData = new FormData();
            Object.entries(form).forEach(([k, v]) => formData.append(k, v));
            formData.append("avatar", avatarFile);

            const res = await Apis.post(endpoints["register"], formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            alert(res.data.message);
            navigate("/login");
        } catch (err) {
            console.error(err);
            if (err.response?.data?.error)
                setError(err.response.data.error);
            else
                setError("Lỗi không xác định khi đăng ký.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <MySpinner />;

    return (
        <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: "70vh" }}>
            <Card style={{ width: "100%", maxWidth: "500px" }} className="p-4 shadow">
                <h3 className="mb-4 text-center">Đăng ký tài khoản</h3>
                <Form onSubmit={handleRegisterRequest} noValidate>
                    <Row>
                        <Col>
                            <Form.Group className="mb-3">
                                <Form.Label>Họ</Form.Label>
                                <Form.Control type="text" name="firstName" value={form.firstName} onChange={handleChange} required />
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="mb-3">
                                <Form.Label>Tên</Form.Label>
                                <Form.Control type="text" name="lastName" value={form.lastName} onChange={handleChange} required />
                            </Form.Group>
                        </Col>
                    </Row>

                    <Form.Group className="mb-3">
                        <Form.Label>Email</Form.Label>
                        <Form.Control type="email" name="email" value={form.email} onChange={handleChange} required />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Địa chỉ</Form.Label>
                        <Form.Control type="text" name="address" value={form.address} onChange={handleChange} required />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Số điện thoại</Form.Label>
                        <Form.Control type="text" name="phoneNumber" value={form.phoneNumber} onChange={handleChange} required />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Username</Form.Label>
                        <Form.Control type="text" name="username" value={form.username} onChange={handleChange} required />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Mật khẩu</Form.Label>
                        <Form.Control type="password" name="password" value={form.password} onChange={handleChange} required />
                        <Form.Text>Ít nhất 6 ký tự, có chữ và số, chỉ chữ & số.</Form.Text>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Xác nhận mật khẩu</Form.Label>
                        <Form.Control type="password" name="confirmPassword" value={form.confirmPassword} onChange={handleChange} required />
                    </Form.Group>

                    <Form.Group className="mb-4">
                        <Form.Label>Ảnh đại diện</Form.Label>
                        <Form.Control type="file" accept="image/*" onChange={handleAvatarChange} required />
                    </Form.Group>

                    <Button type="submit" variant="success" className="w-100" disabled={loading}>
                        {loading ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                Đang xử lý ...
                            </>
                        ) : (
                            "Đăng ký"
                        )}
                    </Button>

                    <MyAlert message={error} onClose={() => setError("")} />
                </Form>
            </Card>
        </Container>
    );
};

export default Register;
