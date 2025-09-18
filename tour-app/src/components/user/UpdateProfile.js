import { useContext, useEffect, useState } from "react";
import { Form, Button, Spinner, Image } from "react-bootstrap";
import { MyUserContext } from "../configs/Context";
import { authApis, endpoints } from "../configs/Apis";
import { toast } from "react-toastify";
import cookies from "react-cookies";

const UpdateProfile = () => {
  const [user] = useContext(MyUserContext);
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    address: "",
    phoneNumber: "",
    companyName: "", // chỉ dùng nếu là PROVIDER
  });
  const [avatarFile, setAvatarFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      setForm({
        firstName: user.firstName || "",
        lastName: user.lastName || "",
        email: user.email || "",
        address: user.address || "",
        phoneNumber: user.phoneNumber || "",
        companyName: user.provider?.companyName || "",
      });
      setPreview(user.avatar);
    }
  }, [user]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAvatarFile(file);
      setPreview(URL.createObjectURL(file));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    for (let key in form) {
      if (form[key]) formData.append(key, form[key]);
    }
    if (avatarFile) formData.append("avatar", avatarFile);

    const endpoint =
      user.role === "PROVIDER"
        ? endpoints["provider-update-profile"]
        : endpoints["user-update-profile"];

    try {
      setLoading(true);
      const res = await authApis(cookies.load("token")).post(endpoint, formData);
      toast.success(res.data.message || "Cập nhật thành công!");
    } catch (err) {
      console.error("Update failed:", err);
      const errorMsg = err.response?.data?.error || "Có lỗi xảy ra khi cập nhật!";
      toast.error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <h3>Cập nhật thông tin cá nhân</h3>
      <Form onSubmit={handleSubmit}>
        <Form.Group className="mb-3">
          <Form.Label>Họ</Form.Label>
          <Form.Control
            type="text"
            name="firstName"
            value={form.firstName}
            onChange={handleChange}
            placeholder="Nhập họ..."
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>Tên</Form.Label>
          <Form.Control
            type="text"
            name="lastName"
            value={form.lastName}
            onChange={handleChange}
            placeholder="Nhập tên..."
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>Email</Form.Label>
          <Form.Control
            type="email"
            name="email"
            value={form.email}
            onChange={handleChange}
            placeholder="example@email.com"
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>Địa chỉ</Form.Label>
          <Form.Control
            type="text"
            name="address"
            value={form.address}
            onChange={handleChange}
            placeholder="123 ABC, TP.HCM"
          />
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label>Số điện thoại</Form.Label>
          <Form.Control
            type="text"
            name="phoneNumber"
            value={form.phoneNumber}
            onChange={handleChange}
            placeholder="090xxxxxxx"
          />
        </Form.Group>

        {user.role === "PROVIDER" && (
          <Form.Group className="mb-3">
            <Form.Label>Tên công ty</Form.Label>
            <Form.Control
              type="text"
              name="companyName"
              value={form.companyName}
              onChange={handleChange}
              placeholder="Công ty TNHH XYZ"
            />
          </Form.Group>
        )}

        <Form.Group className="mb-3">
          <Form.Label>Ảnh đại diện</Form.Label>
          <Form.Control type="file" onChange={handleAvatarChange} accept="image/*" />
          {preview && (
            <div className="mt-2">
              <Image src={preview} thumbnail width="150" height="150" />
            </div>
          )}
        </Form.Group>

        <Button type="submit" disabled={loading}>
          {loading ? (
            <>
              <Spinner animation="border" size="sm" className="me-2" /> Đang cập nhật...
            </>
          ) : (
            "Cập nhật"
          )}
        </Button>
      </Form>
    </>
  );
};

export default UpdateProfile;
