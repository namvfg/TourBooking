import { useState, useMemo } from "react";
import { Form, Button, Card, Container, Row, Col } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import MySpinner from "../layout/MySpinner";
import Apis, { endpoints } from "../configs/Apis";
import { toast } from "react-toastify";

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
    companyName: ""
  });

  const [avatarFile, setAvatarFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [isProvider, setIsProvider] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleAvatarChange = (e) => {
    setAvatarFile(e.target.files?.[0] ?? null);
  };

  const validate = useMemo(
    () => () => {
      if (!form.firstName.trim() || !form.lastName.trim())
        return "Họ và tên không được để trống!";
      if (!EMAIL_PATTERN.test(form.email))
        return "Email không hợp lệ!";
      if (!PHONE_PATTERN.test(form.phoneNumber))
        return "Số điện thoại phải gồm đúng 10 chữ số!";
      if (form.username.length < 3 || form.username.length > 20)
        return "Username phải dài từ 3 đến 20 ký tự!";
      if (!PASSWORD_PATTERN.test(form.password))
        return "Mật khẩu phải từ 6 ký tự, chứa cả chữ và số, và chỉ gồm chữ & số!";
      if (form.password !== form.confirmPassword)
        return "Mật khẩu không khớp!";
      if (!avatarFile)
        return "Vui lòng chọn ảnh đại diện!";
      if (isProvider && !form.companyName.trim())
        return "Vui lòng nhập tên công ty!";
      return "";
    },
    [form, avatarFile, isProvider]
  );

  const handleRegisterRequest = async (e) => {
    e.preventDefault();
    if (loading) return;

    const errMsg = validate();
    if (errMsg) {
      toast.error(errMsg);
      return;
    }

    setLoading(true);
    try {
      const fd = new FormData();
      fd.append("firstName", form.firstName.trim());
      fd.append("lastName", form.lastName.trim());
      fd.append("email", form.email.trim());
      fd.append("username", form.username.trim());
      fd.append("password", form.password);
      fd.append("confirmPassword", form.confirmPassword);
      fd.append("address", form.address.trim());
      fd.append("phoneNumber", form.phoneNumber.trim());
      fd.append("avatar", avatarFile);

      if (isProvider) {
        fd.append("companyName", form.companyName.trim());
      }

      const endpoint = isProvider
        ? endpoints["provider-register"]
        : endpoints["register"];

      const res = await Apis.post(endpoint, fd);
      const data = res?.data ?? {};

      if (data?.success !== true) {
        const msg = data?.error || data?.message || "Đăng ký thất bại.";
        throw new Error(msg);
      }

      toast.success(
        isProvider
          ? "Đăng ký nhà cung cấp thành công! Vui lòng chờ duyệt."
          : "Đăng ký thành công! Bạn có thể đăng nhập."
      );

      navigate("/login");
    } catch (err) {
      console.error("Lỗi đăng ký:", err);
      const serverMsg =
        err?.response?.data?.error ||
        err?.response?.data?.message ||
        err?.message ||
        "Lỗi không xác định khi đăng ký.";
      toast.error(serverMsg);
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

          <Form.Group className="mb-3">
            <Form.Label>Ảnh đại diện</Form.Label>
            <Form.Control type="file" accept="image/*" onChange={handleAvatarChange} required />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Check
              type="checkbox"
              label="Tôi muốn đăng ký làm nhà cung cấp"
              checked={isProvider}
              onChange={(e) => setIsProvider(e.target.checked)}
            />
          </Form.Group>

          {isProvider && (
            <Form.Group className="mb-4">
              <Form.Label>Tên công ty</Form.Label>
              <Form.Control type="text" name="companyName" value={form.companyName} onChange={handleChange} required />
            </Form.Group>
          )}

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
        </Form>
      </Card>
    </Container>
  );
};

export default Register;
