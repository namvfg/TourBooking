import React, { useState } from "react";
import { Modal, Button, Form, Spinner } from "react-bootstrap";
import { CKEditor } from "@ckeditor/ckeditor5-react";
import CustomEditor from '../../ckeditor/build/ckeditor';
import { UploadAdapterPlugin } from "../layout/CustomUploadAdapter";
import cookie from "react-cookies";
import { toast } from "react-toastify";
import { endpoints, authApis } from "../configs/Apis";

const EditServicePostModal = ({ show, onHide, post, onUpdated }) => {
    const [editData, setEditData] = useState(() => ({
        name: post?.name || "",
        description: post?.description || "",
        price: post?.price || "",
        availableSlot: post?.availableSlot || 0,
        address: post?.address || "",
        serviceType: post?.serviceType || "",
        image: null,
        transportType: post?.transportType || "",
        transportStartDate: post?.transportStartDate
            ? new Date(post.transportStartDate).toISOString().slice(0, 16)
            : "",
        destination: post?.destination || "",
        roomStartDate: post?.roomStartDate
            ? new Date(post.roomStartDate).toISOString().slice(0, 16)
            : "",
        roomEndDate: post?.roomEndDate
            ? new Date(post.roomEndDate).toISOString().slice(0, 16)
            : "",
        tourStartDate: post?.tourStartDate
            ? new Date(post.tourStartDate).toISOString().slice(0, 16)
            : "",
        tourEndDate: post?.tourEndDate
            ? new Date(post.tourEndDate).toISOString().slice(0, 16)
            : ""
    }));
    const [loading, setLoading] = useState(false);

    if (!post) return null;

    const handleEditChange = (e) => {
        const { name, value, files } = e.target;
        setEditData((prev) => ({
            ...prev,
            [name]: files ? files[0] : value
        }));
    };

    const handleCKEditorChange = (event, editor) => {
        const data = editor.getData();
        setEditData((prev) => ({
            ...prev,
            description: data
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const url = `${endpoints["service-post-edit"]}/${post.id}`;
            const formData = new FormData();
            formData.append("name", editData.name);
            formData.append("description", editData.description);
            formData.append("price", editData.price);
            formData.append("availableSlot", editData.availableSlot);
            formData.append("address", editData.address);
            formData.append("serviceType", editData.serviceType);
            if (editData.image) formData.append("image", editData.image);

            if (editData.serviceType === "TRANSPORTATION") {
                formData.append("transportType", editData.transportType);
                formData.append("transportStartDate", editData.transportStartDate);
                formData.append("destination", editData.destination);
            }
            if (editData.serviceType === "ROOM") {
                formData.append("roomStartDate", editData.roomStartDate);
                if (editData.roomEndDate) formData.append("roomEndDate", editData.roomEndDate);
            }
            if (editData.serviceType === "TOUR") {
                formData.append("tourStartDate", editData.tourStartDate);
                if (editData.tourEndDate) formData.append("tourEndDate", editData.tourEndDate);
            }

            const token = cookie.load("token");
            await authApis(token).put(url, formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });
            toast.success("Cập nhật thành công!");

            if (onUpdated) onUpdated(); 
            onHide();
        } catch (err) {
            toast.error("Lỗi cập nhật! " + (err?.response?.data || err.message));
        }
        setLoading(false);
    };

    return (
        <Modal show={show} onHide={onHide} size="lg" centered>
            <Modal.Header closeButton>
                <Modal.Title>Cập nhật dịch vụ</Modal.Title>
            </Modal.Header>
            <Form onSubmit={handleSubmit}>
                <Modal.Body>
                    <Form.Group>
                        <Form.Label>Tên dịch vụ</Form.Label>
                        <Form.Control name="name" value={editData.name} onChange={handleEditChange} required />
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Mô tả</Form.Label>
                        <CKEditor
                            editor={CustomEditor}
                            data={editData.description}
                            onChange={handleCKEditorChange}
                            config={{ extraPlugins: [UploadAdapterPlugin] }}
                        />
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Giá</Form.Label>
                        <Form.Control name="price" type="number" min="0" value={editData.price} onChange={handleEditChange} required />
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Số lượng slot</Form.Label>
                        <Form.Control name="availableSlot" type="number" min="0" value={editData.availableSlot} onChange={handleEditChange} required />
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Địa chỉ</Form.Label>
                        <Form.Control name="address" value={editData.address} onChange={handleEditChange} required />
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Loại dịch vụ</Form.Label>
                        <Form.Control name="serviceType" as="select" value={editData.serviceType} onChange={handleEditChange} required>
                            <option value="">Chọn loại</option>
                            <option value="ROOM">Phòng</option>
                            <option value="TOUR">Tour</option>
                            <option value="TRANSPORTATION">Phương tiện</option>
                        </Form.Control>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Ảnh (nếu muốn đổi)</Form.Label>
                        <Form.Control name="image" type="file" accept="image/*" onChange={handleEditChange} />
                    </Form.Group>

                    {editData.serviceType === "ROOM" && (
                        <>
                            <Form.Group>
                                <Form.Label>Ngày bắt đầu phòng</Form.Label>
                                <Form.Control name="roomStartDate" type="datetime-local" value={editData.roomStartDate} onChange={handleEditChange} />
                            </Form.Group>
                            <Form.Group>
                                <Form.Label>Ngày kết thúc phòng</Form.Label>
                                <Form.Control name="roomEndDate" type="datetime-local" value={editData.roomEndDate} onChange={handleEditChange} />
                            </Form.Group>
                        </>
                    )}
                    {editData.serviceType === "TOUR" && (
                        <>
                            <Form.Group>
                                <Form.Label>Ngày bắt đầu tour</Form.Label>
                                <Form.Control name="tourStartDate" type="datetime-local" value={editData.tourStartDate} onChange={handleEditChange} />
                            </Form.Group>
                            <Form.Group>
                                <Form.Label>Ngày kết thúc tour</Form.Label>
                                <Form.Control name="tourEndDate" type="datetime-local" value={editData.tourEndDate} onChange={handleEditChange} />
                            </Form.Group>
                        </>
                    )}
                    {editData.serviceType === "TRANSPORTATION" && (
                        <>
                            <Form.Group>
                                <Form.Label>Loại phương tiện</Form.Label>
                                <Form.Control name="transportType" value={editData.transportType} onChange={handleEditChange} />
                            </Form.Group>
                            <Form.Group>
                                <Form.Label>Ngày khởi hành</Form.Label>
                                <Form.Control name="transportStartDate" type="datetime-local" value={editData.transportStartDate} onChange={handleEditChange} />
                            </Form.Group>
                            <Form.Group>
                                <Form.Label>Điểm đến</Form.Label>
                                <Form.Control name="destination" value={editData.destination} onChange={handleEditChange} />
                            </Form.Group>
                        </>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={onHide}>
                        Đóng
                    </Button>
                    <Button type="submit" variant="primary" disabled={loading}>
                        {loading ? <Spinner animation="border" size="sm" /> : "Lưu thay đổi"}
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
};

export default EditServicePostModal;