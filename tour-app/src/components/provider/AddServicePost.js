import React, { useState, useEffect, useContext } from "react";
import { MyUserContext } from '../configs/Context';
import { authApis, endpoints } from '../configs/Apis';
import { Form, Button, Alert, Spinner } from "react-bootstrap";

const AddServicePost = ({ onAdded }) => {
    const [user] = useContext(MyUserContext);
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [imageFile, setImageFile] = useState(null);      // file ảnh chọn
    const [price, setPrice] = useState("");
    const [availableSlot, setAvailableSlot] = useState("");
    const [address, setAddress] = useState("");
    const [serviceType, setServiceType] = useState("");
    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);
    const [serviceTypeOptions, setServiceTypeOptions] = useState([]);

    // Log user context mỗi lần component render
    React.useEffect(() => {
        console.log("User context hiện tại:", user);
        // Nếu provider tồn tại, log thêm providerId
        if (user?.provider?.id) {
            console.log("ProviderId lấy được từ context:", user.provider.providerId);
        }
    }, [user]);

    // Provider ID từ user context
    const providerId = user?.provider?.providerId;

    // Lấy danh sách loại dịch vụ từ backend khi load form
    useEffect(() => {
        const fetchServiceTypes = async () => {
            try {
                // Đúng endpoint: /enums/service-types
                const res = await authApis().get(endpoints["service-types"]);
                // res.data là mảng ["HOTEL", "TOUR", ...]
                setServiceTypeOptions(res.data);
            } catch (err) {
                setServiceTypeOptions([]);
            }
        };
        fetchServiceTypes();
    }, []);

    // Chỉ nhà cung cấp mới thêm được
    if (!user || user.role !== "PROVIDER") {
        return <Alert variant="danger">Chỉ nhà cung cấp mới được thêm dịch vụ!</Alert>;
    }

    const handleFileChange = (e) => {
        setImageFile(e.target.files?.[0] ?? null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErr(""); setSuccess("");

        // Kiểm tra giá trị đầu vào
        if (!name.trim()) { setErr("Tên dịch vụ không được rỗng!"); return; }
        if (!description.trim()) { setErr("Mô tả không được rỗng!"); return; }
        if (!imageFile) { setErr("Vui lòng chọn ảnh dịch vụ!"); return; }
        if (!price || isNaN(price)) { setErr("Giá phải là số!"); return; }
        if (!availableSlot || isNaN(availableSlot)) { setErr("Số slot phải là số!"); return; }
        if (!address.trim()) { setErr("Địa chỉ không được rỗng!"); return; }
        if (!serviceType) { setErr("Vui lòng chọn loại dịch vụ!"); return; }
        if (!providerId || isNaN(providerId)) { setErr("Provider không hợp lệ!"); return; }

        setLoading(true);

        try {
            const token = user.token;
            const formData = new FormData();
            formData.append("name", name);
            formData.append("description", description);
            formData.append("image", imageFile);
            formData.append("price", price.toString());
            formData.append("availableSlot", parseInt(availableSlot, 10));
            formData.append("address", address);
            formData.append("serviceType", serviceType);
            formData.append("serviceProviderId", parseInt(providerId, 10));

            // Log để debug giá trị gửi lên
            console.log("FormData gửi lên khi submit:", {
                name,
                description,
                imageFile,
                price: price.toString(),
                availableSlot: parseInt(availableSlot, 10),
                address,
                serviceType,
                providerId: parseInt(providerId, 10),
            });

            await authApis(token).post(endpoints["service-post-add"], formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });

            setSuccess("Thêm dịch vụ thành công!");
            setName(""); setDescription(""); setImageFile(null); setPrice(""); setAvailableSlot(""); setAddress(""); setServiceType("");
            if (onAdded) onAdded(); // gọi reload danh sách nếu cần
        } catch (error) {
            let msg = "Lỗi thêm dịch vụ";
            if (error.response?.data && typeof error.response.data === "string" && error.response.data.startsWith("<!DOCTYPE html")) {
                msg = "Yêu cầu không hợp lệ hoặc dữ liệu gửi lên sai định dạng!";
            } else if (error.response?.data) {
                msg = error.response.data;
            }
            setErr(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="mb-5 p-4 border rounded shadow-sm bg-light">
            <h3>Thêm dịch vụ mới</h3>
            {err && <Alert variant="danger">{err}</Alert>}
            {success && <Alert variant="success">{success}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>Tên dịch vụ</Form.Label>
                    <Form.Control value={name} onChange={e => setName(e.target.value)} required />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Mô tả</Form.Label>
                    <Form.Control value={description} onChange={e => setDescription(e.target.value)} as="textarea" rows={2} required />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Ảnh dịch vụ</Form.Label>
                    <Form.Control type="file" accept="image/*" onChange={handleFileChange} required />
                    {imageFile && (
                        <div className="mt-2">
                            <span>Đã chọn: <b>{imageFile.name}</b></span>
                            <img
                                src={URL.createObjectURL(imageFile)}
                                alt="Preview"
                                style={{ maxWidth: 200, marginTop: 10, borderRadius: 8 }}
                            />
                        </div>
                    )}
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Giá (VNĐ)</Form.Label>
                    <Form.Control type="number" value={price} onChange={e => setPrice(e.target.value)} required />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Số slot còn</Form.Label>
                    <Form.Control type="number" value={availableSlot} onChange={e => setAvailableSlot(e.target.value)} required />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Địa chỉ</Form.Label>
                    <Form.Control value={address} onChange={e => setAddress(e.target.value)} required />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>Loại dịch vụ</Form.Label>
                    <Form.Select value={serviceType} onChange={e => setServiceType(e.target.value)} required>
                        <option value="">-- Chọn loại dịch vụ --</option>
                        {serviceTypeOptions.map(type => (
                            <option key={type} value={type}>{type}</option>
                        ))}
                    </Form.Select>
                </Form.Group>
                <Button type="submit" variant="primary" disabled={loading}>
                    {loading ? <Spinner size="sm" /> : "Thêm dịch vụ"}
                </Button>
            </Form>
        </div>
    );
};

export default AddServicePost;