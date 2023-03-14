export function getApiPrefix() {
    let host = process.env.REACT_APP_API_HOST || `${window.location.protocol}//${window.location.hostname}`
    return `${host}${process.env.REACT_APP_API_BASE}`
}