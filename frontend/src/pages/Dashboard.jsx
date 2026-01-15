import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Row, Col, Card, Badge, Table } from 'react-bootstrap';

function Dashboard() {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchData();
        const interval = setInterval(fetchData, 10000);
        return () => clearInterval(interval);
    }, []);

    const fetchData = async () => {
        try {
            // Connect to backend (assuming proxy or direct call)
            const res = await axios.get('http://localhost:8080/dashboard/summary');
            setData(res.data);
            setLoading(false);
        } catch (err) {
            console.error(err);
            setLoading(false);
        }
    };

    if (loading) return <div>Yükleniyor...</div>;
    if (!data) return <div>Veri alınamadı. Backend çalışıyor mu?</div>;

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2>GENEL BAKIŞ</h2>
                    <small className="text-muted">TURKCELL TRUSTSHIELD CORE</small>
                </div>
                <div className="system-status">
                    ● SYSTEM ONLINE
                </div>
            </div>

            {/* Metrics */}
            <Row className="mb-4 g-4">
                <Col md={3}>
                    <div className="neon-card" style={{ borderLeft: '4px solid var(--neon-blue)' }}>
                        <div className="card-header-neon">Toplam Kullanıcı</div>
                        <div className="metric-big text-neon-blue">{data.totalUsers}</div>
                    </div>
                </Col>
                <Col md={3}>
                    <div className="neon-card" style={{ borderLeft: '4px solid var(--neon-green)' }}>
                        <div className="card-header-neon">Düşük Risk</div>
                        <div className="metric-big text-neon-green">{data.lowRiskUsers}</div>
                    </div>
                </Col>
                <Col md={3}>
                    <div className="neon-card" style={{ borderLeft: '4px solid var(--neon-yellow)' }}>
                        <div className="card-header-neon">Orta Risk</div>
                        <div className="metric-big text-neon-yellow">{data.mediumRiskUsers}</div>
                    </div>
                </Col>
                <Col md={3}>
                    <div className="neon-card" style={{ borderLeft: '4px solid var(--neon-red)' }}>
                        <div className="card-header-neon">Yüksek Risk</div>
                        <div className="metric-big text-neon-red">{data.highRiskUsers}</div>
                    </div>
                </Col>
            </Row>

            <Row className="mb-5 g-4">
                <Col md={4}>
                    <div className="neon-card d-flex justify-content-between align-items-center">
                        <span className="card-header-neon mb-0">Açık Case</span>
                        <span className="metric-big" style={{ fontSize: '2.5rem', color: '#fff' }}>{data.openCases}</span>
                    </div>
                </Col>
                <Col md={4}>
                    <div className="neon-card d-flex justify-content-between align-items-center" style={{ borderColor: 'rgba(255, 238, 0, 0.3)' }}>
                        <span className="card-header-neon mb-0 text-neon-yellow">İşlemde</span>
                        <span className="metric-big text-neon-yellow" style={{ fontSize: '2.5rem' }}>{data.inProgressCases}</span>
                    </div>
                </Col>
                <Col md={4}>
                    <div className="neon-card d-flex justify-content-between align-items-center" style={{ borderColor: 'rgba(148, 163, 184, 0.3)' }}>
                        <span className="card-header-neon mb-0 text-muted">Kapalı</span>
                        <span className="metric-big text-muted" style={{ fontSize: '2.5rem' }}>{data.closedCases}</span>
                    </div>
                </Col>
            </Row>

            <Row className="g-4">
                <Col md={6}>
                    <div className="neon-card">
                        <h4 className="mb-4 text-neon-blue">SON EVENTLER</h4>
                        <Table className="table-neon" responsive>
                            <thead>
                                <tr>
                                    <th>Zaman</th>
                                    <th>Kullanıcı</th>
                                    <th>Servis</th>
                                    <th>Tutar</th>
                                </tr>
                            </thead>
                            <tbody>
                                {data.recentEvents.map((e, idx) => (
                                    <tr key={idx}>
                                        <td style={{ color: '#e2e8f0', fontSize: '0.85rem' }}>{new Date(e.timestamp).toLocaleTimeString()}</td>
                                        <td className="fw-bold">{e.userId}</td>
                                        <td className="text-neon-blue">{e.service}</td>
                                        <td className="font-monospace">{e.value} {e.unit}</td>
                                    </tr>
                                ))}
                                {data.recentEvents.length === 0 && <tr><td colSpan="4" className="text-center text-muted">Veri yok</td></tr>}
                            </tbody>
                        </Table>
                    </div>
                </Col>

                <Col md={6}>
                    <div className="neon-card">
                        <h4 className="mb-4 text-neon-pink">SON KARARLAR</h4>
                        <Table className="table-neon" responsive>
                            <thead>
                                <tr>
                                    <th>Zaman</th>
                                    <th>Tetiklenen</th>
                                    <th>Aksiyon</th>
                                </tr>
                            </thead>
                            <tbody>
                                {data.recentDecisions.map((d, idx) => (
                                    <tr key={idx}>
                                        <td style={{ color: '#e2e8f0', fontSize: '0.85rem' }}>{new Date(d.timestamp).toLocaleTimeString()}</td>
                                        <td><small style={{ color: '#fff' }}>{d.triggeredRules.join(', ')}</small></td>
                                        <td><span className="badge-pro info">{d.selectedAction}</span></td>
                                    </tr>
                                ))}
                                {data.recentDecisions.length === 0 && <tr><td colSpan="3" className="text-center text-muted">Veri yok</td></tr>}
                            </tbody>
                        </Table>
                    </div>
                </Col>
            </Row>
        </div>
    );
}

export default Dashboard;
