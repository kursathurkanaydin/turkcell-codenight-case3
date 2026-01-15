import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Form, Button, Card, Alert, Row, Col, Table, Badge } from 'react-bootstrap';

function Events() {
    const [eventData, setEventData] = useState({
        userId: 'U1',
        service: 'Paycell',
        eventType: 'PAYMENT',
        value: 0,
        unit: 'TRY',
        meta: '',
        timestamp: new Date().toISOString()
    });
    const [events, setEvents] = useState([]);
    const [message, setMessage] = useState(null);

    useEffect(() => {
        fetchEvents();
    }, []);

    const fetchEvents = async () => {
        try {
            const res = await axios.get('http://localhost:8080/events');
            // Backend returns List<EventResponse>
            // Sort by timestamp desc or limit
            const sorted = res.data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
            setEvents(sorted);
        } catch (e) {
            console.error("Failed to fetch events", e);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const payload = { ...eventData, timestamp: new Date().toISOString() };
            await axios.post('http://localhost:8080/events', payload);
            setMessage({ type: 'success', text: 'Event başarıyla gönderildi!' });
            fetchEvents(); // Refresh list
        } catch (err) {
            setMessage({ type: 'danger', text: 'Hata: ' + err.message });
        }
    };

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2>EVENT YÖNETİMİ</h2>
                    <small className="text-muted">EVENT SIMULATOR & LOGS</small>
                </div>
            </div>

            <Row>
                {/* EVENT FORM */}
                <Col lg={4} className="mb-4">
                    <div className="neon-card">
                        <h4 className="card-header-neon text-neon-blue mb-4">SİMÜLASYON PANELİ</h4>
                        {message && <Alert variant={message.type} onClose={() => setMessage(null)} dismissible className="bg-dark text-white border-0 shadow">{message.text}</Alert>}
                        <Form onSubmit={handleSubmit}>
                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label className="small text-muted">User ID</Form.Label>
                                        <Form.Control type="text" value={eventData.userId} onChange={e => setEventData({ ...eventData, userId: e.target.value })} placeholder="U1" />
                                    </Form.Group>
                                </Col>
                                <Col md={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label className="small text-muted">Service</Form.Label>
                                        <Form.Control type="text" value={eventData.service} onChange={e => setEventData({ ...eventData, service: e.target.value })} placeholder="Paycell" />
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Form.Group className="mb-3">
                                <Form.Label className="small text-muted">Event Type</Form.Label>
                                <Form.Control type="text" value={eventData.eventType} onChange={e => setEventData({ ...eventData, eventType: e.target.value })} />
                            </Form.Group>

                            <Row>
                                <Col xs={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label className="small text-muted">Value</Form.Label>
                                        <Form.Control type="number" value={eventData.value} onChange={e => setEventData({ ...eventData, value: parseFloat(e.target.value) })} />
                                    </Form.Group>
                                </Col>
                                <Col xs={6}>
                                    <Form.Group className="mb-3">
                                        <Form.Label className="small text-muted">Unit</Form.Label>
                                        <Form.Control type="text" value={eventData.unit} onChange={e => setEventData({ ...eventData, unit: e.target.value })} placeholder="TRY" />
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Form.Group className="mb-4">
                                <Form.Label className="small text-muted">Meta Data</Form.Label>
                                <Form.Control size="sm" type="text" value={eventData.meta} onChange={e => setEventData({ ...eventData, meta: e.target.value })} placeholder="device=new, ip_risk=high" />
                            </Form.Group>

                            <Button type="submit" variant="primary" className="w-100 fw-bold">GÖNDER <i className="ms-1 bi bi-lightning-fill"></i></Button>
                        </Form>
                    </div>
                </Col>

                {/* EVENT LIST */}
                <Col lg={8}>
                    <div className="neon-card">
                        <div className="d-flex justify-content-between align-items-center mb-3">
                            <h4 className="card-header-neon text-neon-pink mb-0">GEÇMİŞ EVENTLER</h4>
                            <Button size="sm" variant="outline-secondary" onClick={fetchEvents}><i className="bi bi-arrow-clockwise"></i></Button>
                        </div>
                        <div className="table-responsive">
                            <Table className="table-neon">
                                <thead>
                                    <tr>
                                        <th>ZAMAN</th>
                                        <th>KULLANICI</th>
                                        <th>SERVİS</th>
                                        <th>TİP</th>
                                        <th>TUTAR</th>
                                        <th>META</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {events.length === 0 ? (
                                        <tr><td colSpan="6" className="text-center py-4 text-muted">Henüz event yok.</td></tr>
                                    ) : events.map((e, idx) => (
                                        <tr key={idx}>
                                            <td style={{ color: '#e2e8f0', fontSize: '0.85rem', whiteSpace: 'nowrap' }}>{new Date(e.timestamp).toLocaleTimeString()}</td>
                                            <td><span className="fw-bold text-white">{e.userId}</span></td>
                                            <td><span className="text-neon-blue">{e.service}</span></td>
                                            <td><span className="badge-pro warning">{e.eventType}</span></td>
                                            <td className="text-white font-monospace">{e.value} {e.unit}</td>
                                            <td className="small text-muted font-monospace">{e.meta || '-'}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </div>
                    </div>
                </Col>
            </Row>
        </div>
    );
}

export default Events;
