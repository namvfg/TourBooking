import React, { useState } from "react";
import { Modal } from "react-bootstrap";
import ChatList from "./ChatList";
import ChatBox from "../user/ChatBox";


const ChatMessengerModal = ({ show, onHide, mode, currentId, currentUser }) => {
  const [selectedChat, setSelectedChat] = useState(null);

  return (
    <Modal show={show} onHide={onHide} size="xl" centered>
      <Modal.Header closeButton>
        <Modal.Title>Hộp thư</Modal.Title>
      </Modal.Header>
      <Modal.Body style={{ minHeight: 500, maxHeight: 700, overflow: "hidden", padding: 0 }}>
        <div style={{ display: "flex", height: 500 }}>
          <div style={{ width: 300, borderRight: "1px solid #ddd", overflowY: "auto" }}>
            <ChatList
              mode={mode}
              currentId={currentId}
              currentUser={currentUser}
              onSelectChat={setSelectedChat}
              selectedChat={selectedChat}
            />
          </div>
          <div style={{ flex: 1, position: "relative", minWidth: 0 }}>
            {selectedChat ? (
              <ChatBox
                userId={selectedChat.userId}
                providerId={selectedChat.providerId}
                userName={selectedChat.userName}
                providerName={selectedChat.providerName}
                myId={currentUser.id}
                role={mode === "provider" ? "PROVIDER" : "USER"}
                onClose={() => setSelectedChat(null)}
                inMessenger={true}
                avatarCurrentUser={currentUser?.avatar || "/default-avatar.png"}
              />
            ) : (
              <div style={{ padding: 30 }}>Chọn một hội thoại để xem tin nhắn</div>
            )}
          </div>
        </div>
      </Modal.Body>
    </Modal>
  );
};

export default ChatMessengerModal;