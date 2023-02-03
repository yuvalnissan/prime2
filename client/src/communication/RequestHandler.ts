import { getScenarioURL, getPauseURL, getResumeURL, getMessageURL, getResetURL } from './urls'

const postToUrl = async (url: string, body: any) => {
    const response = await fetch(url, {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
        },
    })

    if (!response.ok) {
        throw new Error(`Error! status: ${response.status}`)
    }
}

const getFromUrl = async (url: string) => {
    const response = await fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
        },
    })

    if (!response.ok) {
        throw new Error(`Error! status: ${response.status}`)
    }

    const result = await response.json()

    return result
}

export class RequestHandler {
    private setShouldRefresh: Function
    scenarioName: string
    agentName: string

    constructor(scenarioName: string, agentName: string, setShouldRefresh: Function) {
        this.scenarioName = scenarioName
        this.agentName = agentName
        this.setShouldRefresh = setShouldRefresh
    }

    private async sendAgentMessage(type: string, id: string) {
        console.log('Sending message', type, id)
        try {
            await postToUrl(getMessageURL(this.scenarioName, this.agentName), {type, id})
            this.setShouldRefresh(true)
        } catch (err) {
            console.error(`Failed sending message ${type}`, err)
            window.alert('Failed sending message')
        } 
    }

    async sendAddData(dataToAdd: string) {
        await this.sendAgentMessage('add-data', dataToAdd)
    }

    async sendIgnite(id: string) {
        await this.sendAgentMessage('ignite', id)
    }

    async sendSensePositive(id: string) {
        await this.sendAgentMessage('sense-positive', id)
    }

    async sendSenseNegative(id: string) {
        await this.sendAgentMessage('sense-negative', id)
    }

    async sendPause() {
        await postToUrl(getPauseURL(this.scenarioName), {})
    }

    async sendResume() {
        await postToUrl(getResumeURL(this.scenarioName), {})
    }

    async sendReset() {
        await postToUrl(getResetURL(this.scenarioName), {})
    }

    async getScenario() {
        return await getFromUrl(getScenarioURL(this.scenarioName, this.agentName))
    }
}
