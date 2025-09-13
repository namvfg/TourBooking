import React, { useState, useContext } from "react";
import ServicePostList from "./ServicePostList";
import AddServicePost from "./provider/AddServicePost";
import { MyUserContext } from './configs/Context';
import { Button } from "react-bootstrap";

const Home = () => {
    const [user] = useContext(MyUserContext);
    const [showAddForm, setShowAddForm] = useState(false);
    const [reloadCount, setReloadCount] = useState(0);

    const handleOpenForm = () => setShowAddForm(true);
    const handleCloseForm = () => {
        setShowAddForm(false);
        setReloadCount(c => c + 1); // tăng biến mỗi lần thêm mới
    };

    return (
        <div>
            {user && user.role === "PROVIDER" && !showAddForm && (
                <Button variant="primary" onClick={handleOpenForm}>
                    Thêm dịch vụ
                </Button>
            )}
            {user && user.role === "PROVIDER" && showAddForm && (
                <div className="my-4">
                    <AddServicePost onAdded={handleCloseForm} />
                    <Button variant="secondary" className="mt-2" onClick={handleCloseForm}>
                        Đóng
                    </Button>
                </div>
            )}
            <ServicePostList reloadCount={reloadCount} user={user} />
        </div>
    );
}

export default Home;