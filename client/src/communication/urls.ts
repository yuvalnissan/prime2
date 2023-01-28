
export const getScenarioURL = (scenarioName: string, agentName: string) => `http://localhost:8080/scenario?name=${scenarioName}&agent=${agentName}`

export const getMessageURL = (scenarioName: string, agentName: string) => `http://localhost:8080/message?name=${scenarioName}&agent=${agentName}`

export const getResetURL = (scenarioName: string) => `http://localhost:8080/reset?name=${scenarioName}`

export const getPauseURL = (scenarioName: string) => `http://localhost:8080/pause?name=${scenarioName}`

export const getResumeURL = (scenarioName: string) => `http://localhost:8080/resume?name=${scenarioName}`

export const getAddDataURL = (scenarioName: string, agentName: string) => `http://localhost:8080/newData?name=${scenarioName}&agent=${agentName}`

export const postToUrl = async (url: string, body: any) => {
    const response = await fetch(url, {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
        },
    })

    if (!response.ok) {
        throw new Error(`Error! status: ${response.status}`);
    }
}
